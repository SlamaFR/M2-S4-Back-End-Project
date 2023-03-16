package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.follow.FollowedUserRepository
import com.kamelia.ugeoverflow.tag.Tag
import com.kamelia.ugeoverflow.user.User
import com.kamelia.ugeoverflow.user.UserRepository
import com.kamelia.ugeoverflow.user.toLightDTO
import com.kamelia.ugeoverflow.utils.currentUser
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import java.util.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import kotlin.collections.ArrayDeque


@Component
class QuestionPageService(
    private val questionRepository: QuestionRepository,
    private val followedUserRepository: FollowedUserRepository,
    private val entityManagerFactory: EntityManagerFactory,
    private val userRepository: UserRepository,
) {

    @Transactional
    fun questionsAsUser(user: User, page: Pageable, filters: QuestionSearchFilterDTO?): Page<QuestionLightDTO> {
        val encounteredUsers = mutableSetOf(user.id)
        val followedStillToVisit = ArrayDeque<UUID>()

        val toVisit = ArrayDeque(userRepository.findAllUsersFollowedByUserId(user.id))
        println("toVisit: $toVisit")
        val toDisplay = ArrayList<QuestionLightDTO>()
        var toSkip = page.pageNumber * page.pageSize

        while (toDisplay.size < page.pageSize && (toVisit.isNotEmpty() || followedStillToVisit.isNotEmpty())) {
            if (toVisit.isEmpty()) { // we have visited all the users we follow, we need to visit another circle of users
                // we load the circle of the first user of the queue while filtering out the users we have already visited
                val circleRoot = followedStillToVisit.removeFirst()
                toVisit += userRepository.findAllUsersFollowedByUserId(circleRoot, encounteredUsers)
            }

            val current = toVisit.removeFirst()
            val currentId: UUID = current.id

            encounteredUsers += currentId
            followedStillToVisit += currentId

            if (toSkip > 0) { // we need to skip the questions of the previous pages
                val questionCount = countUserQuestions(currentId, filters)
                if (toSkip >= questionCount) { // the user has fewer questions than we need to skip, so we skip all of them
                    toSkip -= questionCount
                    continue
                }

                // otherwise, we skip the first questions of the user and keep the last ones
                toDisplay += getUserQuestions(  // we add the questions we need to display
                    currentId,
                    toSkip,
                    page.pageSize - toDisplay.size,
                    filters
                )
                toSkip = 0 // we don't need to skip anymore
                continue
            }

            toDisplay += getUserQuestions(
                currentId,
                0,
                page.pageSize - toDisplay.size,
                filters
            ) // we add the questions we need to display
        }

        if (toDisplay.size < page.pageSize) { // the graph of the users we follow is not big enough, we need to add questions from other users
            TODO("Yep")
        }

        return PageImpl(toDisplay, page, toDisplay.size.toLong())
    }

    @Transactional
    fun questionsAsAnonymous(page: Pageable): Page<QuestionLightDTO> =
        questionRepository.findAllAsAnonymous(page).map(Question::toLightDTO)

    private fun getUserQuestions(
        authorId: UUID,
        skip: Int,
        max: Int,
        filters: QuestionSearchFilterDTO?,
    ): List<QuestionLightDTO> {
        val entityManager = entityManagerFactory.createEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Question::class.java)
        val root = criteriaQuery.from(Question::class.java)

        val actualFilters = filters?.toPredicate(root, criteriaQuery, criteriaBuilder) ?: criteriaBuilder.conjunction()

        criteriaQuery.select(root)
            .where(
                criteriaBuilder.equal(root.get<UUID>("author"), authorId),
                actualFilters
            )
            .orderBy(criteriaBuilder.desc(root.get<UUID>("creationDate")))

        val query = entityManager.createQuery(criteriaQuery).apply {
            if (max >= 0) {
                maxResults = max
            }
            firstResult = skip
        }

        return query.resultStream
            .map(Question::toLightDTO)
            .toList()
    }

    private fun countUserQuestions(
        authorId: UUID,
        filters: QuestionSearchFilterDTO?,
    ): Int {
        val entityManager = entityManagerFactory.createEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Long::class.java)
        val root = criteriaQuery.from(Question::class.java)

        val actualFilters = filters?.toPredicate(root, criteriaQuery, criteriaBuilder) ?: criteriaBuilder.conjunction()
        criteriaQuery.select(criteriaBuilder.count(root))
            .where(
                criteriaBuilder.equal(root.get<UUID>("author"), authorId),
                actualFilters
            )
            .orderBy(criteriaBuilder.desc(root.get<UUID>("creationDate")))

        return entityManager.createQuery(criteriaQuery).singleResult.toInt()
    }


    fun QuestionSearchFilterDTO.toPredicate(
        root: Root<Question>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder,
    ): Predicate {
        val titlePredicate = title?.let {
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")),
                "%${it.lowercase(Locale.getDefault())}%"
            )
        } ?: criteriaBuilder.conjunction()

        val tagsPredicate = tags?.let {
            criteriaBuilder.equal(
                root.joinSet<Question, Tag>("tags").get<String>("name"),
                tags
            )
        } ?: criteriaBuilder.conjunction()

        return criteriaBuilder.and(titlePredicate, tagsPredicate)
    }

    @Transactional
    fun dummy() = run {
        val user = userRepository.findById(currentUser().id).get()
        val randomIgnored = user.followed.random().followed
        userRepository
            .findAllUsersFollowedByUserId(currentUser().id, setOf(randomIgnored.id))
            .map(User::toLightDTO)
    }

}
