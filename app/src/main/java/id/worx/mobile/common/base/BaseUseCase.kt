package id.worx.mobile.common.base

abstract class BaseUseCase<Request, Response> {

    abstract suspend fun execute(request: Request): Response
}