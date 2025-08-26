package ua.privatbank.liqpay.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ua.privatbank.liqpay.sample.databinding.ActivityGooglePayTokenExampleBinding
import ua.privatbank.liqpay.base.EXTRA_LIQPAY_RESULT
import ua.privatbank.liqpay.base.LiqpayActivityResult
import ua.privatbank.liqpay.googlepay.GooglePayProvider

class GooglePayTokenExampleActivity : AppCompatActivity() {

    private val googlePayProvider = GooglePayProvider()
    private val googleTokenPayActivityResultLauncher =
        registerForActivityResult(googlePayProvider.createActivityResultContract()) {
            handleActivityResult(it)
        }
    private val viewModel: GooglePayTokenExampleViewModel by viewModels {
        GooglePayTokenExampleViewModel.getFactory(googlePayProvider)
    }

    private var _binding: ActivityGooglePayTokenExampleBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityGooglePayTokenExampleBinding.inflate(layoutInflater)
                .also { _binding = it }
                .root
        )
        binding.bInit.setOnClickListener {
            viewModel.onInitClick()
        }
        viewModel.secondAndThirdStepAvailabilityLiveData.observe(this) {
            binding.etGooglePayToken.isEnabled = it
            binding.bPay.isEnabled = it
        }
        viewModel.progressData.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
        viewModel.errorLiveData.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
        }
        binding.bPay.setOnClickListener {
            viewModel.onPayClick(binding.etGooglePayToken.text.toString())
        }
        viewModel.startPayLiveData.observe(this) {
            googleTokenPayActivityResultLauncher.launch(it)
        }
    }

    private fun handleActivityResult(it: ActivityResult) {
        if (it.resultCode == RESULT_OK) {
            val intent = it.data ?: return
            val result =
                intent.getSerializableExtra(EXTRA_LIQPAY_RESULT) as LiqpayActivityResult
            val text = "Got result OK with data: $result"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        } else if (it.resultCode == RESULT_CANCELED) {
            val text = "Got RESULT_CANCELED"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }
}