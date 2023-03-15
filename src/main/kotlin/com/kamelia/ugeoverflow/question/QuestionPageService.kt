package com.kamelia.ugeoverflow.question

import com.kamelia.ugeoverflow.user.User
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component


@Component
class QuestionPageService(
    private val questionRepository: QuestionRepository,
    private val entityManagerFactory: EntityManagerFactory,
) {

    @Transactional
    fun questionsAsUser(user: User, page: Pageable): Page<QuestionLightDTO> {
        val entityManager = entityManagerFactory.createEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Question::class.java).run {
            select(from(Question::class.java))
        }

        val query = entityManager.createQuery(criteriaQuery).apply {
            firstResult = page.pageNumber * page.pageSize
            maxResults = 1//page.pageSize
        }

        val result = query.resultList.map(Question::toLightDTO)
        return PageImpl(result, page, result.size.toLong())
    }

    @Transactional
    fun questionsAsAnonymous(page: Pageable): Page<QuestionLightDTO> =
        questionRepository.findAllAsAnonymous(page).map(Question::toLightDTO)

}
