package com.kamelia.ugeoverflow.utils

import jakarta.servlet.http.HttpServletRequest

object Routes {

    object Api {
        const val ROOT = "/api/v1"

        object User {

            const val ROOT = "${Api.ROOT}/users"

            const val REGISTER = "$ROOT/register"

            const val LOGIN = "$ROOT/login"

            const val REFRESH = "$ROOT/refresh"

            const val LOGOUT = "$ROOT/logout"

            const val LOGOUT_ALL = "$ROOT/logout-all"

            const val UPDATE_PASSWORD = "$ROOT/update-password"

        }

        object Following {

            const val ROOT = "${Api.ROOT}/follows"

        }

        object Tag {

            const val ROOT = "${Api.ROOT}/tags"

        }

        object Question {

            const val ROOT = "${Api.ROOT}/questions"

        }

        object Answer {

            const val ROOT = "${Api.ROOT}/answers"

        }

    }

    object Auth {

        const val ROOT = "/auth"

        const val LOGIN = "$ROOT/login"

        const val REGISTER = "$ROOT/register"

        const val LOGOUT = "$ROOT/logout"

        const val REFRESH = "$ROOT/refresh"

    }

    object Question {

        const val ROOT = "/question"

        const val VOTE = "$ROOT/vote"

        const val UNVOTE = "$ROOT/unvote"

        const val DELETE = "$ROOT/delete"

        const val COMMENT = "$ROOT/comment"

        const val CREATE = "$ROOT/create"

        object Answer {

            const val ROOT = "${Question.ROOT}/answer"

            const val DELETE = "$ROOT/delete"

            const val COMMENT = "$ROOT/comment"

        }

    }

    object User {

        const val ROOT = "/user"

        const val EVALUATE = "$ROOT/evaluate"

        const val FOLLOW = "$ROOT/follow"

        const val UNFOLLOW = "$ROOT/unfollow"

        object Details {

            const val ROOT = "${User.ROOT}/details"

            const val PASSWORD = "$ROOT/password"

        }

    }

    object Error {

        const val ROOT = "/error"

    }

}

val HttpServletRequest.referer: String?
    get() = this.getHeader("referer")