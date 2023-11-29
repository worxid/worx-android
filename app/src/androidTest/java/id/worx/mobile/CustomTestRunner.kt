package id.worx.mobile

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.CustomTestApplication
import id.worx.mobile.HiltTestApplication_Application

@CustomTestApplication(AppCore::class)
interface HiltTestApplication

class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication_Application::class.java.name, context)
    }
}