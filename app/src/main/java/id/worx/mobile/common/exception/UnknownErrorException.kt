package id.worx.mobile.common.exception

import id.worx.mobile.common.GENERAL_ERROR_MESSAGE

class UnknownErrorException(val error: String = GENERAL_ERROR_MESSAGE) : Throwable(error)