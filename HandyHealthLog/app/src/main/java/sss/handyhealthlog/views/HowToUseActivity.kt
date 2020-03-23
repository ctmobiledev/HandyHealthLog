package sss.handyhealthlog.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_how_to_use.*
import sss.handyhealthlog.R

class HowToUseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        webView1.loadUrl("file:///android_asset/howtotext.html");

    }
}
