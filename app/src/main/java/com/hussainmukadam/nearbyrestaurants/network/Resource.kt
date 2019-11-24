package com.hussainmukadam.nearbyrestaurants.network

enum class Status {
    SUCCESS,
    ERROR,
    LOADING,
    NO_INTERNET;

    /**
     * Returns `true` if the [Status] is success else `false`.
     */
    fun isSuccessful() = this == SUCCESS

    /**
     * Returns `true` if the [Status] is loading else `false`.
     */
    fun isLoading() = this == LOADING
}


data class Resource<ResultType>(var status: Status, var data: ResultType? = null, var errorMessage: String? = null, var loading: Boolean = false) {

    companion object {
        /**
         * Creates [Resource] object with `SUCCESS` status and [data].
         */
        fun <ResultType> success(data: ResultType): Resource<ResultType> =
            Resource(Status.SUCCESS, data)

        /**
         * Creates [Resource] object with `LOADING` status to notify
         * the UI to showing loading.
         */
        fun <ResultType> loading(flag: Boolean): Resource<ResultType> =
            Resource(Status.LOADING, loading = flag)

        /**
         * Creates [Resource] object with `ERROR` status and [message].
         */
        fun <ResultType> error(message: String?): Resource<ResultType> =
            Resource(Status.ERROR, errorMessage = message)

        fun <ResultType> noInternetConnection(): Resource<ResultType> = Resource(Status.NO_INTERNET)
    }
}
