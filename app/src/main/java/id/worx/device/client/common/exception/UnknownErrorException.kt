package id.worx.device.client.common.exception

import id.worx.device.client.common.GENERAL_ERROR_MESSAGE

class UnknownErrorException(val error: String = GENERAL_ERROR_MESSAGE) : Throwable(error)