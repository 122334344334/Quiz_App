package com.example.quiz_app_final; // Ersetze dies durch deinen tatsächlichen Paketnamen

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
 import android.widget.Toast; // Für Debugging, wenn nötig

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewQuizTitle;
    private TextView textViewQuestionNumber;
    private TextView textViewQuestion;
    private Button buttonOption1;
    private Button buttonOption2;
    private Button buttonOption3;
    private Button buttonOption4;
    private ProgressBar progressBar;
    private TextView textViewFeedback;
    private Button buttonNextOrRestart;

    private String[] questions;
    private List<List<String>> allOptions;
    private int[] correctAnswerIndices;
    private String currentQuizType;
    private String quizTitleString;


    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private final int TOTAL_QUESTIONS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        currentQuizType = intent.getStringExtra(StartActivity.EXTRA_QUIZ_TYPE);

        Log.d("QUIZ_DEBUG", "Intent extras: " + getIntent().getExtras());
        Log.d("QUIZ_DEBUG", "currentQuizType: " + currentQuizType);

        textViewQuizTitle = findViewById(R.id.textViewQuizTitle);
        textViewQuestionNumber = findViewById(R.id.textViewQuestionNumber);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        buttonOption1 = findViewById(R.id.buttonOption1);
        buttonOption2 = findViewById(R.id.buttonOption2);
        buttonOption3 = findViewById(R.id.buttonOption3);
        buttonOption4 = findViewById(R.id.buttonOption4);
        progressBar = findViewById(R.id.progressBar);
        textViewFeedback = findViewById(R.id.textViewFeedback);
        buttonNextOrRestart = findViewById(R.id.buttonNextOrRestart);

        buttonOption1.setOnClickListener(this);
        buttonOption2.setOnClickListener(this);
        buttonOption3.setOnClickListener(this);
        buttonOption4.setOnClickListener(this);
        buttonNextOrRestart.setOnClickListener(this);


        if (currentQuizType == null) {
            // Fallback, sollte nicht passieren, wenn StartActivity korrekt funktioniert
             Toast.makeText(this, "Quiztyp nicht gefunden!", Toast.LENGTH_LONG).show();
            finish(); // Beende die Activity, wenn kein Quiztyp vorhanden ist
            return;
        }

        loadQuizData();
        startQuiz();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_quiz), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ... (Code von oben bleibt gleich bis zum Ende von onCreate)

    private void loadQuizData() {
        allOptions = new ArrayList<>(); // Initialisiere hier

        switch (currentQuizType) {
            case StartActivity.QUIZ_TYPE_CAPITALS:
                quizTitleString = getString(R.string.capitals_quiz_title);
                questions = getResources().getStringArray(R.array.questions_capitals);
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_capitals_1)));
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_capitals_2)));
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_capitals_3)));
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_capitals_4)));
                correctAnswerIndices = getResources().getIntArray(R.array.correct_indices_capitals);
                break;
            case StartActivity.QUIZ_TYPE_HISTORY:
                quizTitleString = getString(R.string.history_quiz_title);
                questions = getResources().getStringArray(R.array.questions_history);
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_history_1)));
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_history_2)));
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_history_3)));
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_history_4)));
                correctAnswerIndices = getResources().getIntArray(R.array.correct_indices_history);
                break;
            case StartActivity.QUIZ_TYPE_INVENTORS:
                quizTitleString = getString(R.string.inventors_quiz_title);
                questions = getResources().getStringArray(R.array.questions_inventors);
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_inventors_1)));
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_inventors_2)));
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_inventors_3)));
                allOptions.add(Arrays.asList(getResources().getStringArray(R.array.options_inventors_4)));
                correctAnswerIndices = getResources().getIntArray(R.array.correct_indices_inventors);
                break;
            default:
                // Fallback, falls ein unbekannter Quiztyp übergeben wird
                 Toast.makeText(this, "Unbekannter Quiztyp!", Toast.LENGTH_LONG).show();
                finish(); // Beende die Activity
                return;
        }
        textViewQuizTitle.setText(String.format(getString(R.string.quiz_activity_title_placeholder), quizTitleString));
        progressBar.setMax(TOTAL_QUESTIONS);
    }

    private void startQuiz() {
        currentQuestionIndex = 0;
        correctAnswersCount = 0;
        progressBar.setProgress(0);
        textViewFeedback.setText(getString(R.string.default_feedback_text));
        buttonNextOrRestart.setEnabled(false);
        buttonNextOrRestart.setText(getString(R.string.next_question_button_text)); // Standardtext für den Button
        displayQuestion();
        enableAnswerButtons(true);
    }

    private void displayQuestion() {
        if (questions == null || allOptions.isEmpty() || currentQuestionIndex >= questions.length) {
            // Verhindere Absturz, wenn Daten nicht korrekt geladen wurden oder Index außerhalb der Grenzen ist
             Toast.makeText(this, "Fehler beim Laden der Frage.", Toast.LENGTH_SHORT).show();
            finish(); // Zurück zum Startbildschirm
            return;
        }

        if (currentQuestionIndex < TOTAL_QUESTIONS) {
            textViewQuestionNumber.setText(getString(R.string.question_number_prefix) + " " + (currentQuestionIndex + 1));
            textViewQuestion.setText(questions[currentQuestionIndex]);

            List<String> currentOptions = allOptions.get(currentQuestionIndex);
            if (currentOptions.size() >= 4) { // Sicherstellen, dass genügend Optionen vorhanden sind
                buttonOption1.setText(currentOptions.get(0));
                buttonOption2.setText(currentOptions.get(1));
                buttonOption3.setText(currentOptions.get(2));
                buttonOption4.setText(currentOptions.get(3));
            } else {
                // Fehlerbehandlung, falls nicht genügend Optionen für eine Frage vorhanden sind
                 Toast.makeText(this, "Zu wenige Optionen für Frage " + (currentQuestionIndex + 1), Toast.LENGTH_SHORT).show();
                finish(); // Zurück zum Startbildschirm
                return;
            }

            textViewFeedback.setText(getString(R.string.default_feedback_text));
            buttonNextOrRestart.setEnabled(false);
            buttonNextOrRestart.setText(getString(R.string.next_question_button_text)); // Zurücksetzen für neue Frage
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.buttonOption1 || viewId == R.id.buttonOption2 || viewId == R.id.buttonOption3 || viewId == R.id.buttonOption4) {
            Button clickedButton = (Button) v;
            String selectedAnswerText = clickedButton.getText().toString();
            checkAnswer(selectedAnswerText);
        } else if (viewId == R.id.buttonNextOrRestart) {
            handleNextOrRestart();
        }
    }

    private void checkAnswer(String selectedAnswerText) {
        enableAnswerButtons(false);
        buttonNextOrRestart.setEnabled(true);

        if (correctAnswerIndices == null || currentQuestionIndex >= correctAnswerIndices.length ||
                allOptions.isEmpty() || currentQuestionIndex >= allOptions.size() ||
                allOptions.get(currentQuestionIndex).size() <= correctAnswerIndices[currentQuestionIndex]) {
            // Verhindere Absturz, wenn Daten nicht korrekt geladen wurden
             Toast.makeText(this, "Fehler beim Überprüfen der Antwort.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int correctIndexForCurrentQuestion = correctAnswerIndices[currentQuestionIndex];
        String correctAnswerText = allOptions.get(currentQuestionIndex).get(correctIndexForCurrentQuestion);

        if (selectedAnswerText.equals(correctAnswerText)) {
            correctAnswersCount++;
            progressBar.setProgress(correctAnswersCount);
            textViewFeedback.setText(String.format(getString(R.string.correct_answer_feedback), selectedAnswerText));

            if (currentQuestionIndex == TOTAL_QUESTIONS - 1) { // Letzte Frage
                if (correctAnswersCount == TOTAL_QUESTIONS) {
                    textViewFeedback.append("\n" + getString(R.string.all_questions_correct));
                }
                buttonNextOrRestart.setText(getString(R.string.restart_quiz_button_text)); // "Zurück zum Start"
            } else {
                buttonNextOrRestart.setText(getString(R.string.next_question_button_text));
            }
        } else {
            textViewFeedback.setText(String.format(getString(R.string.wrong_answer_feedback), selectedAnswerText));
            buttonNextOrRestart.setText(getString(R.string.restart_quiz_button_text)); // "Zurück zum Start"
        }
    }

    private void handleNextOrRestart() {
        if (buttonNextOrRestart.getText().toString().equals(getString(R.string.restart_quiz_button_text))) {
            // Zurück zur StartActivity. finish() beendet diese QuizActivity.
            // StartActivity wird dann wieder aus dem Backstack geholt oder neu erstellt.
            finish();
        } else { // "Nächste Frage"
            currentQuestionIndex++;
            if (currentQuestionIndex < TOTAL_QUESTIONS) {
                displayQuestion();
                enableAnswerButtons(true);
                // buttonNextOrRestart.setEnabled(false); // Wird in displayQuestion() gehandhabt
            } else {
                // Sollte durch die Logik in checkAnswer abgedeckt sein,
                // wo der Button auf "Zurück zum Start" gesetzt wird.
                finish();
            }
        }
    }

    private void enableAnswerButtons(boolean enable) {
        buttonOption1.setEnabled(enable);
        buttonOption2.setEnabled(enable);
        buttonOption3.setEnabled(enable);
        buttonOption4.setEnabled(enable);
    }
}
