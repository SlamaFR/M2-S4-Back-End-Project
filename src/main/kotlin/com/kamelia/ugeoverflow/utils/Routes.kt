package com.kamelia.ugeoverflow.utils

object Routes {

    const val API_ROUTES_ROOT = "/api/v1"

    object User {

        const val ROOT = "$API_ROUTES_ROOT/users"

        const val REGISTER = "$ROOT/register"

        const val LOGIN = "$ROOT/login"

        const val REFRESH = "$ROOT/refresh"

        const val LOGOUT = "$ROOT/logout"

        const val LOGOUT_ALL = "$ROOT/logout-all"

        const val UPDATE_PASSWORD = "$ROOT/update-password"

    }

    object Following {

        const val ROOT = "$API_ROUTES_ROOT/follows"

    }

    object Tag {

        const val ROOT = "$API_ROUTES_ROOT/tags"

    }

}
