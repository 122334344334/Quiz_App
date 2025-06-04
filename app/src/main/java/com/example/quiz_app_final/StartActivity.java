package com.example.quiz_app_final; // Ersetze dies durch deinen tatsächlichen Paketnamen

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartActivity extends AppCompatActivity {

    private Button buttonSelectQuiz;
    private Button buttonStartQuiz;
    private TextView textViewSelectedQuiz; // Optional: um das ausgewählte Quiz anzuzeigen

    public static final String EXTRA_QUIZ_TYPE = "com.example.quiz_app_final.QUIZ_TYPE";
    public static final String QUIZ_TYPE_CAPITALS = "capitals";
    public static final String QUIZ_TYPE_HISTORY = "history";
    public static final String QUIZ_TYPE_INVENTORS = "inventors";

    private String selectedQuizType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        buttonSelectQuiz = findViewById(R.id.buttonSelectQuiz);
        buttonStartQuiz = findViewById(R.id.buttonStartQuiz);
        textViewSelectedQuiz = findViewById(R.id.textViewSelectedQuiz); // Optional

        // Initial Feedback-Text leeren oder Standard setzen
        textViewSelectedQuiz.setText(""); // Oder einen Platzhalter, wenn nichts ausgewählt ist

        buttonSelectQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuizSelectionMenu(v);
            }
        });

        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedQuizType != null) {
                    Intent intent = new Intent(StartActivity.this, QuizActivity.class);
                    intent.putExtra(EXTRA_QUIZ_TYPE, selectedQuizType);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    // Sollte nicht passieren, da der Button deaktiviert ist, aber als Fallback
                    Toast.makeText(StartActivity.this, getString(R.string.no_quiz_selected_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // WindowInsets für EdgeToEdge Handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_start), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showQuizSelectionMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenuInflater().inflate(R.menu.quiz_selection_menu, popup.getMenu()); // Erfordert eine menu.xml Datei

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_item_capitals) {
                    selectedQuizType = QUIZ_TYPE_CAPITALS;
                    updateSelectedQuizFeedback(getString(R.string.menu_capitals));
                    return true;
                } else if (itemId == R.id.menu_item_history) {
                    selectedQuizType = QUIZ_TYPE_HISTORY;
                    updateSelectedQuizFeedback(getString(R.string.menu_history));
                    return true;
                } else if (itemId == R.id.menu_item_inventors) {
                    selectedQuizType = QUIZ_TYPE_INVENTORS;
                    updateSelectedQuizFeedback(getString(R.string.menu_inventors));
                    return true;
                } else {
                    return false;
                }
            }
        });
        popup.show();
    }

    private void updateSelectedQuizFeedback(String quizName) {
        textViewSelectedQuiz.setText(String.format(getString(R.string.selected_quiz_placeholder), quizName));
        buttonStartQuiz.setEnabled(true); // "Quiz starten"-Button aktivieren
    }

    // Stelle sicher, dass du eine Menü-Ressourcendatei erstellst
    // res/menu/quiz_selection_menu.xml
}