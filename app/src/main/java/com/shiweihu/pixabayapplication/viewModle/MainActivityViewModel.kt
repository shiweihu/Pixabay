package com.shiweihu.pixabayapplication.viewModle

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager
import com.shiweihu.pixabayapplication.room.SysSetting
import com.shiweihu.pixabayapplication.room.SysSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val sysSettingRepository: SysSettingRepository
) :ViewModel(){


    suspend fun initSysSetting(){
        viewModelScope.launch{
            sysSettingRepository.sysSettingDao.initSystemSetting(SysSetting(0))
        }
    }

    suspend fun getSyssetting() = sysSettingRepository.getSysSetting()
    fun setTimes(activity:Activity,setting: SysSetting){
        viewModelScope.launch(Dispatchers.IO) {
            sysSettingRepository.setSysSetting(setting.startTimes+1)
        }
        if(setting.startTimes > 3 && !setting.isRating){
            startGooglePlayRating(activity)
        }
    }

    private fun startGooglePlayRating(activity: Activity){
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                    viewModelScope.launch(Dispatchers.IO){
                        sysSettingRepository.setIsRating(true)
                    }
                }
            } else {
                // There was some problem, log or handle the error code.
                task.exception?.printStackTrace()
            }
        }
    }
}