package com.example.medibot

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import androidx.appcompat.app.AppCompatActivity

class FirstAidActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid)

        val firstAidTextView = findViewById<TextView>(R.id.firstAidTextView)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        val content = """
            ✅ Cuts and Bleeding:
            - Clean the wound with water.
            - Apply gentle pressure with sterile gauze.
            - Dress with a clean bandage.

            🔥 Burns:
            - Run cool (not cold) water on the area.
            - Cover with non-cotton gauze.
            - Do not pop blisters.

            🦶 Sprained Limbs:
            - Rest the limb and apply ice.
            - Use a compression bandage.
            - Elevate the limb.

            💧 Diarrhea:
            - Hydrate with water and tender coconut water.
            - Avoid salty/sugary drinks.
            - Eat bland, gut-friendly foods.

            🤕 Falls/Accidents:
            - Don’t move.
            - Wait for help unless critical.

            😮‍💨 Choking:
            - Use back blows or abdominal thrusts.
            - For babies, give 5 firm back blows face-down.

            🌊 Drowning/Cardiac:
            - Start CPR immediately.
            - 100–120 chest compressions per minute.
            - Keep airway open if trained.

            ⚠️ Anaphylaxis:
            - Use an epinephrine auto-injector (EpiPen).
            - Call emergency services.

            ⚡ Electrical Shock:
            - Do NOT touch if contact persists.
            - If not breathing: start CPR.
            - Cover burns with sterile bandage.

            🧠 Stroke:
            - Signs: Confusion, trouble speaking, facial droop.
            - Get to hospital within the golden hour.
            - Lay them on their side, loosen clothes.

            🚑 Stay calm, act smart, and know your first aid!
        """.trimIndent()

        firstAidTextView.text = content
    }
}
