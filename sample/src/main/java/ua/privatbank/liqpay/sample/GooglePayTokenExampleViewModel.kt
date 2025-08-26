package ua.privatbank.liqpay.sample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ua.privatbank.liqpay.cardpay.GooglePayTokenInputData
import ua.privatbank.liqpay.cardpay.data.GooglePayInitResponse
import ua.privatbank.liqpay.googlepay.GooglePayProvider

class GooglePayTokenExampleViewModel(private val googlePayProvider: GooglePayProvider) :
    ViewModel() {

    companion object {
        fun getFactory(googlePayProvider: GooglePayProvider): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    GooglePayTokenExampleViewModel(
                        googlePayProvider
                    )
                }
            }
        }
    }

    val secondAndThirdStepAvailabilityLiveData = MutableLiveData<Boolean>()
    val progressData = MutableLiveData<Boolean>()
    val errorLiveData = SingleLiveData<Exception>()
    val startPayLiveData = SingleLiveData<GooglePayTokenInputData>()

    private var googlePayInitResponse: GooglePayInitResponse? = null

    fun onInitClick() {
        progressData.value = true
        googlePayProvider
            .init(
                PaymentData.dataCardPayGooglePayToken.first,
                PaymentData.dataCardPayGooglePayToken.second
            )
            .onSuccess {
                googlePayInitResponse = it
                secondAndThirdStepAvailabilityLiveData.value = true
            }
            .onError {
                errorLiveData.postValue(it)
            }
            .onTerminate {
                progressData.value = false
            }
    }

    fun onPayClick(googlePayToken: String) {
        if (googlePayToken.isEmpty()) return
        startPayLiveData.value = GooglePayTokenInputData(
            googlePayToken,
            googlePayInitResponse?.liqpayToken ?: return
        )
    }

    override fun onCleared() {
        super.onCleared()
        googlePayProvider.onDestroy()
    }
}