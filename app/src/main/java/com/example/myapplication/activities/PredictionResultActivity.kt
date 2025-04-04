package com.example.medibot

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.min
import android.util.Log
private const val err= "PredictionDebug"


class PredictionResultActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private lateinit var allSymptoms: List<String>  // Full symptom list used during training
    private val topN = 5  // Top N predictions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)
        Log.d(err, "PredictionResultActivity started")

        val resultText = findViewById<TextView>(R.id.predictionResultText)
        val selectedSymptoms = intent.getStringArrayListExtra("selected_symptoms") ?: arrayListOf()

        Log.d(err, "Selected symptoms: $selectedSymptoms")

        loadSymptoms() // Load full symptom list
        Log.d(err, "All symptoms loaded: size = ${allSymptoms.size}")

        try {
            interpreter = Interpreter(loadModelFile("disease_model.tflite"))
            Log.d(err, "Model loaded successfully")
        } catch (e: Exception) {
            Log.e(err, "Failed to load model: ${e.message}")
            resultText.text = "Error loading model."
            return
        }

        val inputArray = convertSymptomsToInput(selectedSymptoms)
        Log.d(err, "Input array created: ${inputArray[0].joinToString()}")

        val outputArray = Array(1) { FloatArray(NUM_CLASSES) }

        Log.d("PredictionDebug", "Model inference run started")
        interpreter.run(inputArray, outputArray)
        Log.d("PredictionDebug", "Model inference completed")
        Log.d(err, "Input array length: ${inputArray[0].size}, Expected: ${allSymptoms.size}")

        val predictions = getTopPredictions(outputArray[0])

        Log.d(err, "Top predictions: $predictions")
        val expected = 132
        if (allSymptoms.size != expected) {
            Log.e(err, "Symptom count mismatch: Expected $expected, Found ${allSymptoms.size}")
        }
        val expectedInputSize = interpreter.getInputTensor(0).shape()[1]
        Log.d(err, "Input array length: ${inputArray[0].size}, Model expects: $expectedInputSize")


        resultText.text = "Selected Symptoms:\n${selectedSymptoms.joinToString(", ")}\n\nPredictions:\n" +
                predictions.joinToString("\n") { "${it.first}: ${(it.second * 100).toInt()}%" }
    }

    private fun loadModelFile(modelFileName: String): MappedByteBuffer {
        val fileDescriptor = assets.openFd(modelFileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun loadSymptoms() {
        allSymptoms = listOf(
            "abdominal_pain", "abnormal_menstruation", "acidity", "acute_liver_failure",
            "altered_sensorium", "anxiety", "back_pain", "belly_pain", "blackheads",
            "bladder_discomfort", "blister", "blood_in_sputum", "bloody_stool",
            "blurred_and_distorted_vision", "breathlessness", "brittle_nails", "bruising",
            "burning_micturition", "chest_pain", "chills", "cold_hands_and_feets", "coma",
            "congestion", "constipation", "continuous_feel_of_urine", "continuous_sneezing",
            "cough", "cramps", "dark_urine", "dehydration", "depression", "diarrhoea",
            "dischromic _patches", "distention_of_abdomen", "dizziness",
            "drying_and_tingling_lips", "enlarged_thyroid", "excessive_hunger",
            "extra_marital_contacts", "family_history", "fast_heart_rate", "fatigue",
            "fluid_overload", "foul_smell_of urine", "headache", "high_fever",
            "hip_joint_pain", "history_of_alcohol_consumption", "increased_appetite",
            "indigestion", "inflammatory_nails", "internal_itching", "irregular_sugar_level",
            "irritability", "irritation_in_anus", "joint_pain", "knee_pain",
            "lack_of_concentration", "lethargy", "loss_of_appetite", "loss_of_balance",
            "loss_of_smell", "malaise", "mild_fever", "mood_swings", "movement_stiffness",
            "mucoid_sputum", "muscle_pain", "muscle_wasting", "muscle_weakness", "nausea",
            "neck_pain", "nodal_skin_eruptions", "obesity", "pain_behind_the_eyes",
            "pain_during_bowel_movements", "pain_in_anal_region", "painful_walking",
            "palpitations", "passage_of_gases", "patches_in_throat", "phlegm", "polyuria",
            "prominent_veins_on_calf", "puffy_face_and_eyes", "pus_filled_pimples",
            "receiving_blood_transfusion", "receiving_unsterile_injections",
            "red_sore_around_nose", "red_spots_over_body", "redness_of_eyes", "restlessness",
            "runny_nose", "rusty_sputum", "scurring", "shivering", "silver_like_dusting",
            "sinus_pressure", "skin_peeling", "skin_rash", "slurred_speech",
            "small_dents_in_nails", "spinning_movements", "spotting_ urination",
            "stiff_neck", "stomach_bleeding", "stomach_pain", "sunken_eyes", "sweating",
            "swelled_lymph_nodes", "swelling_joints", "swelling_of_stomach",
            "swollen_blood_vessels", "swollen_extremeties", "swollen_legs",
            "throat_irritation", "toxic_look_(typhos)", "ulcers_on_tongue", "unsteadiness",
            "visual_disturbances", "vomiting", "watering_from_eyes", "weakness_in_limbs",
            "weakness_of_one_body_side", "weight_gain", "weight_loss", "yellow_crust_ooze",
            "yellow_urine", "yellowing_of_eyes", "yellowish_skin", "itching"
        )
    }


    private fun convertSymptomsToInput(selected: List<String>): Array<FloatArray> {
        val input = FloatArray(allSymptoms.size)
        selected.forEach { symptom ->
            val idx = allSymptoms.indexOf(symptom)
            if (idx >= 0) input[idx] = 1f
        }
        return arrayOf(input)
    }


    private fun getTopPredictions(output: FloatArray): List<Pair<String, Float>> {
        val labelMap = listOf(
            "(vertigo) Paroymsal  Positional Vertigo",
            "AIDS",
            "Acne",
            "Alcoholic hepatitis",
            "Allergy",
            "Arthritis",
            "Bronchial Asthma",
            "Cervical spondylosis",
            "Chicken pox",
            "Chronic cholestasis",
            "Common Cold",
            "Dengue",
            "Diabetes",
            "Dimorphic hemmorhoids(piles)",
            "Drug Reaction",
            "Fungal infection",
            "GERD",
            "Gastroenteritis",
            "Heart attack",
            "Hepatitis B",
            "Hepatitis C",
            "Hepatitis D",
            "Hepatitis E",
            "Hypertension",
            "Hyperthyroidism",
            "Hypoglycemia",
            "Hypothyroidism",
            "Impetigo",
            "Jaundice",
            "Malaria",
            "Migraine",
            "Osteoarthristis",
            "Paralysis (brain hemorrhage)",
            "Peptic ulcer diseae",
            "Pneumonia",
            "Psoriasis",
            "Tuberculosis",
            "Typhoid",
            "Urinary tract infection",
            "Varicose veins",
            "hepatitis A"
        )

        val indexed = output.mapIndexed { index, prob -> index to prob }
        val top = indexed.sortedByDescending { it.second }.take(topN)
        return top.map { labelMap[it.first] to it.second }
    }

    companion object {
        private const val NUM_CLASSES = 41 // Replace with actual number of diseases in your dataset
    }
}
