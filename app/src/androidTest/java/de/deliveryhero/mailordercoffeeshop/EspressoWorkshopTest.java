package de.deliveryhero.mailordercoffeeshop;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsAnything.anything;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoWorkshopTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private String mailOrderButtonText = "Submit order";
    private String emailID = "vinayakashegde@blah.com";
    private String customerName = "Vinayaka";
    private String milk_spacer = "Steamed";
    private int numberOfEspressoShots = 2;
    private String orderName = numberOfEspressoShots + " Espresso shots";
    private String orderDetails = "Ingredients:\n" + numberOfEspressoShots + " shots of espresso\nChocolate\nSteamed Low fat";

    //Define Matcher to be used with items search
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent) &&
                        view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public void closeScreen() {
        //Assertion here to make sure that close button exists
        ViewInteraction introductionPageCloseButton = onView((withId(R.id.close_button)));
        introductionPageCloseButton.check(matches(isDisplayed()));


        //Click the close button on introduction page
        ViewInteraction appCompatImageButton = onView((withId(R.id.close_button)));
        appCompatImageButton.perform(click());

    }

    public void addEspresso(int numberOfEspressoShots) {
        for (int i = 0; i < numberOfEspressoShots; i++) {
            onView(withId(R.id.more_espresso)).perform(click());
        }
    }

    public void placeOrder() {
        addEspresso(numberOfEspressoShots);
        onView(withId(R.id.chocolate)).perform(click());

        //Select Low fat from Milk selection dropdown
        onData(anything()).inAdapterView(withId(R.id.milk_type)).atPosition(2).perform();

        // Click the Steamed radio button
        onView(withText(milk_spacer)).perform(click());

        //Since  Review Order button is located at the bottom and is not visible need to scroll the page.
        onView(withId(R.id.review_order_button)).perform(scrollTo(), click());
    }

    public void validateOrder() {
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.beverage_detail_ingredients), withText(orderDetails),
                        childAtPosition(
                                allOf(withId(R.id.beverage_detail_ingredients_container),
                                        childAtPosition(
                                                withId(R.id.beverage_detail_container),
                                                0)),
                                3),
                        isDisplayed()));
        textView2.check(matches(withText(orderDetails)));
    }


    public void submitOrder() {
        ViewInteraction textInputEditText = onView((withId(R.id.name_text_box)));
        textInputEditText.perform(replaceText(customerName), closeSoftKeyboard());


        ViewInteraction emailTextBox = onView((withId(R.id.email_text_box)));
        emailTextBox.perform(replaceText(emailID), closeSoftKeyboard());

        onView(withId(R.id.custom_order_name_box)).perform(scrollTo(), typeText(orderName));


        ViewInteraction submitOrder = onView(allOf(withId(R.id.mail_order_button), withText(mailOrderButtonText)));
        submitOrder.perform(scrollTo(), click());
    }

    @Before
    public void performMandatoryStepsforTests() {
        closeScreen();
    }

    @Test
    public void espressoWorkShopTask1() {
        placeOrder();
        validateOrder();
        submitOrder();
    }
}
