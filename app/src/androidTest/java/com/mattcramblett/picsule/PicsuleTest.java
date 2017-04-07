package com.mattcramblett.picsule;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.robotium.solo.Solo;

@SuppressWarnings("rawtypes")
public class PicsuleTest extends ActivityInstrumentationTestCase2 {

    private Solo solo;
    public PicsuleTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * NOTE: Sign out before running tests
     */

    /**
     * @throws Exception
     */
    public void testLoginGoesToMenuThenLogout() throws Exception {
        solo.unlockScreen();
        solo.waitForActivity(LoginActivity.class, 2000);

        // check that we have the right activity
        solo.assertCurrentActivity("Expected Login activity", LoginActivity.class);

        EditText emailField = (EditText) solo.getCurrentActivity().findViewById(R.id.field_email);
        EditText passwordField = (EditText) solo.getCurrentActivity().findViewById(R.id.field_password);

        //fill fields
        solo.enterText(emailField, "matt@matt.com");
        solo.enterText(passwordField, "Password");

        //click login
        Button loginBtn = (Button) solo.getCurrentActivity().findViewById(R.id.email_sign_in_button);
        solo.clickOnView(loginBtn);

        solo.waitForActivity(MenuActivity.class, 2000);
        solo.assertCurrentActivity("Expected Menu activity", MenuActivity.class);

        //Sign out
        Button logoutBtn = (Button) solo.getCurrentActivity().findViewById(R.id.logout_button);
        solo.clickOnView(logoutBtn);

        solo.waitForActivity(LoginActivity.class, 2000);
        solo.assertCurrentActivity("Expected Login activity", LoginActivity.class);

        Button signoutBtn = (Button) solo.getCurrentActivity().findViewById(R.id.sign_out_button);
        solo.clickOnView(signoutBtn);
    }

    /**
     * @throws Exception
     */
    public void testLoginGoesToMenuThenNearby() throws Exception {
        solo.unlockScreen();
        solo.waitForActivity(LoginActivity.class, 2000);

        // check that we have the right activity
        solo.assertCurrentActivity("Expected Login activity", LoginActivity.class);

        EditText emailField = (EditText) solo.getCurrentActivity().findViewById(R.id.field_email);
        EditText passwordField = (EditText) solo.getCurrentActivity().findViewById(R.id.field_password);

        //fill fields
        solo.enterText(emailField, "matt@matt.com");
        solo.enterText(passwordField, "Password");

        //click login
        Button loginBtn = (Button) solo.getCurrentActivity().findViewById(R.id.email_sign_in_button);
        solo.clickOnView(loginBtn);

        solo.waitForActivity(MenuActivity.class, 2000);
        solo.assertCurrentActivity("Expected Menu activity", MenuActivity.class);



        //Click button
        Button nearbyBtn = (Button) solo.getCurrentActivity().findViewById(R.id.nearby_button);
        solo.clickOnView(nearbyBtn);

        //Explore Activity
        solo.waitForActivity(NearbyActivity.class, 2000);
        solo.assertCurrentActivity("Expected Nearby activity", NearbyActivity.class);


        //Go back
        solo.goBack();

        //logout
        solo.waitForActivity(MenuActivity.class, 2000);
        solo.assertCurrentActivity("Expected Menu activity", MenuActivity.class);

        //Sign out
        Button logoutBtn = (Button) solo.getCurrentActivity().findViewById(R.id.logout_button);
        solo.clickOnView(logoutBtn);

        solo.waitForActivity(LoginActivity.class, 2000);
        solo.assertCurrentActivity("Expected Login activity", LoginActivity.class);

        Button signoutBtn = (Button) solo.getCurrentActivity().findViewById(R.id.sign_out_button);
        solo.clickOnView(signoutBtn);
    }


}