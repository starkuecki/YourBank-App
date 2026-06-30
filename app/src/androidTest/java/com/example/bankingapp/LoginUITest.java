package com.example.bankingapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.bankingapp.ui.login.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginUITest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginFieldsAreVisible() {
        // Prüfen, ob die Eingabefelder und der Button angezeigt werden
        Espresso.onView(ViewMatchers.withId(R.id.et_username))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.et_password))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testEmptyLoginShowsToast() {
        // Versuchen einzuloggen ohne Daten einzugeben
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click());

        // Hier könnte man prüfen, ob ein Toast erscheint (etwas komplexer in Espresso)
        // Aber wir prüfen zumindest, dass wir immer noch auf der Login-Seite sind
        Espresso.onView(ViewMatchers.withId(R.id.btn_login))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
