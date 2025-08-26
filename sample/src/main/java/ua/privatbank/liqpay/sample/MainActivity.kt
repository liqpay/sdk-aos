package ua.privatbank.liqpay.sample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import ua.privatbank.liqpay.LiqPay
import ua.privatbank.liqpay.base.EXTRA_LIQPAY_RESULT
import ua.privatbank.liqpay.base.LiqpayActivityResult
import ua.privatbank.liqpay.cardpay.InputData

class MainActivity : AppCompatActivity() {

    private val cardPayActivityResultLauncher =
        registerForActivityResult(LiqPay.cardPay().createActivityResultContract()) {
            handleActivityResult(it)
        }

    private val tokenPayActivityResultLauncher =
        registerForActivityResult(LiqPay.tokenPay().createActivityResultContract()) {
            handleActivityResult(it)
        }

    private fun handleActivityResult(it: ActivityResult) {
        if (it.resultCode == RESULT_OK) {
            val intent = it.data ?: return
            val result =
                intent.getSerializableExtra(EXTRA_LIQPAY_RESULT) as LiqpayActivityResult
            val text = "Got result OK with data: $result"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            tvResult.text = text
        } else if (it.resultCode == RESULT_CANCELED) {
            val text = "Got RESULT_CANCELED"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            tvResult.text = text
        }
    }

    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPrivatPay = findViewById<Button>(R.id.btnPrivatPay)
        val btnLiqpayCard = findViewById<Button>(R.id.btnLiqpayCard)
        val btnLiqpayToken = findViewById<Button>(R.id.btnLiqpayToken)
        val btnGooglePay = findViewById<Button>(R.id.btnGooglePay)

        tvResult = findViewById(R.id.tvResult)

        btnPrivatPay.setOnClickListener {
            LiqPay.p24Pay(this)
                .confirmPay(
                    PaymentData.p24PayDataAndSignature.first,
                    PaymentData.p24PayDataAndSignature.second
                )
                .onSuccess {
                    Handler(Looper.getMainLooper()).post {
                        tvResult.text = "onSuccess p24 pay"
                    }
                }
                .onError {
                    Handler(Looper.getMainLooper()).post {
                        tvResult.text = "onError p24 pay; {$it}";
                    }
                }
        }
        btnLiqpayCard.setOnClickListener {
            cardPayActivityResultLauncher.launch(
                InputData(
                    PaymentData.dataCardPay.first,
                    PaymentData.dataCardPay.second
                )
            )
        }

        btnLiqpayToken.setOnClickListener {
            tokenPayActivityResultLauncher.launch(
                InputData(
                    PaymentData.dataLiqpayToken.first,
                    PaymentData.dataLiqpayToken.second
                )
            )
        }

        btnGooglePay.setOnClickListener {
            startActivity(Intent(this, GooglePayTokenExampleActivity::class.java))
        }
    }
}