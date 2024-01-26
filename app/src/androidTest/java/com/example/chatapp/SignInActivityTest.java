package com.example.chatapp;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.chatapp.activities.SigninActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

    @Rule
    public ActivityScenarioRule<SigninActivity> activityRule =
            new ActivityScenarioRule<>(SigninActivity.class);

    @Test
    public void signInWithValidCredentials() {
        // Type email and password
        Espresso.onView(ViewMatchers.withId(R.id.inputEmail))
                .perform(ViewActions.typeText("khalil@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.inputPassword))
                .perform(ViewActions.typeText("1234567"));

        // Close soft keyboard
        Espresso.closeSoftKeyboard();

        // Click the SignIn button
        Espresso.onView(ViewMatchers.withId(R.id.buttonSingIn))
                .perform(ViewActions.click());

        // You can add assertions or verifications here based on your application's behavior.
        // For example, check if a new activity is launched or if a certain view is displayed.
    }

    @Test
    public void signInWithInvalidCredentials() {
        // Type invalid email and password
        Espresso.onView(ViewMatchers.withId(R.id.inputEmail))
                .perform(ViewActions.typeText("invalid@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.inputPassword))
                .perform(ViewActions.typeText("123456789"));

        // Close soft keyboard
        Espresso.closeSoftKeyboard();

        // Click the SignIn button
        Espresso.onView(ViewMatchers.withId(R.id.buttonSingIn))
                .perform(ViewActions.click());

        // You can add assertions or verifications here based on your application's behavior.
        // For example, check if an error message is displayed.
    }
}
