package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.core.InvalidRequestException
import com.kamelia.ugeoverflow.tag.Tag
import com.kamelia.ugeoverflow.tag.TagRepository
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.user.UserRepository
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import java.time.Instant
import java.util.Locale
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class QuestionPageListService(
    private val entityManagerFactory: EntityManagerFactory,
    private val userRepository: UserRepository, private val tagRepository: TagRepository,
) {

    @Transactional
    fun questionsAsUser(user: User, page: Pageable, filters: QuestionSearchFilterDTO?): Page<QuestionLightDTO> {
        val (toDisplay, usersToIgnore) = retrieveFollowedUsersQuestions(user, page, filters)
        println(toDisplay)
        // the graph of the users we follow is not big enough, we need to add questions from other users
        if (toDisplay.size < page.pageSize) {
            println(usersToIgnore)
            toDisplay += getQuestionsFromUsersNotInSet(page.pageSize - toDisplay.size, filters, usersToIgnore)
        }
        println(toDisplay)

        return PageImpl(toDisplay, page, toDisplay.size.toLong())
    }

    @Transactional
    fun questionsAsAnonymous(page: Pageable, filters: QuestionSearchFilterDTO?): Page<QuestionLightDTO> {
        val skip = page.pageNumber * page.pageSize
        val result = getQuestions(skip, page.pageSize, filters)
        return PageImpl(result, page, result.size.toLong())
    }

    private fun retrieveFollowedUsersQuestions(
        user: User,
        page: Pageable,
        filters: QuestionSearchFilterDTO?,
    ): Pair<MutableList<QuestionLightDTO>, Set<UUID>> {
        val toDisplay = ArrayList<QuestionLightDTO>()
        val followedStillToVisit = ArrayDeque<UUID>()
        val encounteredUsers = mutableSetOf(user.id)

        val toVisit = ArrayDeque(userRepository.findAllUsersFollowedByUserId(user.id))
        var leftToSkip = page.pageNumber * page.pageSize

        while (toDisplay.size < page.pageSize && (toVisit.isNotEmpty() || followedStillToVisit.isNotEmpty())) {
            if (toVisit.isEmpty()) { // we have visited all the users we follow, we need to visit another circle of users
                // we load the circle of the first user of the queue while filtering out the users we have already visited
                val circleRoot = followedStillToVisit.removeFirst()
                val toAdd = userRepository.findAllUsersFollowedByUserId(circleRoot, encounteredUsers)
                if (toAdd.isEmpty()) continue // the user has no followers, we need to skip him
                toVisit += toAdd
            }

            val current = toVisit.removeFirst()
            val currentId = current.id
            val max = page.pageSize - toDisplay.size

            encounteredUsers += currentId
            followedStillToVisit += currentId

            if (leftToSkip > 0) { // we need to skip the questions of the previous pages
                val questionCount = countUserQuestions(currentId, filters)
                if (leftToSkip >= questionCount) { // the user has fewer questions than we need to skip, so we skip all of them
                    leftToSkip -= questionCount
                    continue
                }

                // otherwise, we skip the first questions of the user and keep the last ones
                toDisplay += getQuestionsFromUser(  // we add the questions we need to display
                    leftToSkip,
                    max,
                    filters,
                    currentId
                )
                leftToSkip = 0 // we don't need to skip anymore
                continue
            }

            toDisplay += getQuestionsFromUser( // we add the questions we need to display
                0,
                max,
                filters,
                currentId
            )
        }

        encounteredUsers -= user.id // we remove the user from the encountered users to display his questions
        return toDisplay to encounteredUsers
    }

    private fun getQuestionsFromUser(
        skip: Int,
        max: Int,
        filters: QuestionSearchFilterDTO?,
        authorId: UUID,
    ): List<QuestionLightDTO> = getQuestions(skip, max, filters) { b, r, _ ->
        b.equal(r.get<UUID>("author"), authorId)
    }

    private fun getQuestionsFromUsersNotInSet(
        max: Int,
        filters: QuestionSearchFilterDTO?,
        usersToExclude: Set<UUID>,
    ): List<QuestionLightDTO> = getQuestions(0, max, filters) { b, r, _ ->
        b.not(r.get<UUID>("author").`in`(usersToExclude))
    }

    private fun getQuestions(
        skip: Int,
        max: Int,
        filters: QuestionSearchFilterDTO?,
        extraFilters: (CriteriaBuilder, Root<Question>, CriteriaQuery<Question>) -> Predicate = { b, _, _ -> b.conjunction() },
    ): List<QuestionLightDTO> = entityManagerFactory.createEntityManager().use {
        val criteriaBuilder = it.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Question::class.java)
        val root = criteriaQuery.from(Question::class.java)

        val actualFilters = filters.toPredicate(root, criteriaQuery, criteriaBuilder)

        criteriaQuery.select(root)
            .where(actualFilters, extraFilters(criteriaBuilder, root, criteriaQuery))
            .orderBy(criteriaBuilder.desc(root.get<Instant>("creationDate")))

        val query = it.createQuery(criteriaQuery).apply {
            if (max >= 0) {
                maxResults = max
            }
            firstResult = skip
        }

        query.resultList.map(Question::toLightDTO)
    }

    private fun countUserQuestions(
        authorId: UUID,
        filters: QuestionSearchFilterDTO?,
    ): Int = entityManagerFactory.createEntityManager().use {
        val criteriaBuilder = it.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Long::class.java)
        val root = criteriaQuery.from(Question::class.java)

        val actualFilters = filters.toPredicate(root, criteriaQuery, criteriaBuilder)
        criteriaQuery.select(criteriaBuilder.count(root))
            .where(
                criteriaBuilder.equal(root.get<UUID>("author"), authorId),
                actualFilters
            )
            .orderBy(criteriaBuilder.desc(root.get<Instant>("creationDate")))

        it.createQuery(criteriaQuery).singleResult.toInt()
    }


    fun QuestionSearchFilterDTO?.toPredicate(
        root: Root<Question>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder,
    ): Predicate {
        if (this == null) return criteriaBuilder.conjunction()
        val titlePredicate = title?.let {
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")),
                "%${it.lowercase(Locale.getDefault())}%"
            )
        } ?: criteriaBuilder.conjunction()

        val tagsPredicate = tags?.let {
            val actualTags = tagRepository.findAllByNameIn(it) // retrieve the tags from the database
            if (actualTags.size != it.size) { // ensure that all the tags are valid
                throw InvalidRequestException.badRequest("Invalid tags provided")
            }

            val tagFilterArray = arrayOfNulls<Predicate>(it.size) // create an array of predicates
            actualTags.forEachIndexed { i, tag ->
                // convert each tag to a predicate and add it to the array
                val predicate = criteriaBuilder.isMember(tag, root.get<Set<Tag>>("_tags"))
                tagFilterArray[i] = predicate
            }
            criteriaBuilder.and(*tagFilterArray)
        } ?: criteriaBuilder.conjunction()

        return criteriaBuilder.and(titlePredicate, tagsPredicate)
    }

}
