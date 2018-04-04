/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.litho.processor.integration.resources;

import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.Px;
import com.facebook.litho.BaseMatcher;
import com.facebook.litho.BaseMatcherBuilder;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.testing.subcomponents.InspectableComponent;
import javax.annotation.Nullable;
import org.assertj.core.api.Condition;
import org.assertj.core.api.Java6Assertions;
import org.assertj.core.description.TextDescription;

/**
 * @prop-required child com.facebook.litho.Component
 * @prop-required myDimenSizeProp float
 * @prop-required myRequiredColorProp int
 * @prop-required myStringProp java.lang.String
 * @see com.facebook.litho.processor.integration.resources.BasicTestSampleSpec
 */
public final class BasicTestSample implements BasicTestSampleSpec {
  public static Matcher matcher(ComponentContext c) {
    return new Matcher(c);
  }

  public static class Matcher extends BaseMatcher<Matcher> {
    @Nullable Condition<InspectableComponent> mChildComponentMatcher;

    @Nullable org.hamcrest.Matcher<Component> mChildMatcher;

    @Nullable org.hamcrest.Matcher<Float> mMyDimenSizePropMatcher;

    @Nullable
    org.hamcrest.Matcher<Integer> mMyRequiredColorPropMatcher;

    @Nullable org.hamcrest.Matcher<String> mMyStringPropMatcher;

    Matcher(ComponentContext c) {
      super.init(c, c.getResourceCache());
    }

    public Matcher child(Condition<InspectableComponent> matcher) {
      mChildComponentMatcher = matcher;
      return this;
    }

    public Matcher child(org.hamcrest.Matcher<Component> matcher) {
      mChildMatcher = (org.hamcrest.Matcher<com.facebook.litho.Component>) matcher;
      return this;
    }

    public Matcher child(Component child) {
      this.mChildMatcher = org.hamcrest.core.Is.is((Component) child);
      return this;
    }

    public Matcher child(Component.Builder<?> childBuilder) {
      this.mChildMatcher = org.hamcrest.core.Is.is((Component) childBuilder.build());
      return this;
    }

    public Matcher myDimenSizeProp(org.hamcrest.Matcher<Float> matcher) {
      mMyDimenSizePropMatcher = (org.hamcrest.Matcher<java.lang.Float>) matcher;
      return this;
    }

    public Matcher myDimenSizePropPx(@Px float myDimenSizeProp) {
      this.mMyDimenSizePropMatcher = org.hamcrest.core.Is.is((float) myDimenSizeProp);
      return this;
    }

    public Matcher myDimenSizePropRes(@DimenRes int resId) {
      this.mMyDimenSizePropMatcher = org.hamcrest.core.Is.is((float) resolveDimenSizeRes(resId));
      return this;
    }

    public Matcher myDimenSizePropAttr(@AttrRes int attrResId, @DimenRes int defResId) {
      this.mMyDimenSizePropMatcher = org.hamcrest.core.Is.is((float) resolveDimenSizeAttr(attrResId, defResId));
      return this;
    }

    public Matcher myDimenSizePropAttr(@AttrRes int attrResId) {
      this.mMyDimenSizePropMatcher = org.hamcrest.core.Is.is((float) resolveDimenSizeAttr(attrResId, 0));
      return this;
    }

    public Matcher myDimenSizePropDip(@Dimension(unit = Dimension.DP) float dips) {
      this.mMyDimenSizePropMatcher = org.hamcrest.core.Is.is((float) dipsToPixels(dips));
      return this;
    }

    public Matcher myRequiredColorProp(org.hamcrest.Matcher<Integer> matcher) {
      mMyRequiredColorPropMatcher = (org.hamcrest.Matcher<java.lang.Integer>) matcher;
      return this;
    }

    public Matcher myRequiredColorProp(@ColorInt int myRequiredColorProp) {
      this.mMyRequiredColorPropMatcher = org.hamcrest.core.Is.is((int) myRequiredColorProp);
      return this;
    }

    public Matcher myRequiredColorPropRes(@ColorRes int resId) {
      this.mMyRequiredColorPropMatcher = org.hamcrest.core.Is.is((int) resolveColorRes(resId));
      return this;
    }

    public Matcher myRequiredColorPropAttr(@AttrRes int attrResId, @ColorRes int defResId) {
      this.mMyRequiredColorPropMatcher =
          org.hamcrest.core.Is.is((int) resolveColorAttr(attrResId, defResId));
      return this;
    }

    public Matcher myRequiredColorPropAttr(@AttrRes int attrResId) {
      this.mMyRequiredColorPropMatcher =
          org.hamcrest.core.Is.is((int) resolveColorAttr(attrResId, 0));
      return this;
    }

    public Matcher myStringProp(org.hamcrest.Matcher<String> matcher) {
      mMyStringPropMatcher = (org.hamcrest.Matcher<java.lang.String>) matcher;
      return this;
    }

    public Matcher myStringProp(String myStringProp) {
      this.mMyStringPropMatcher = org.hamcrest.core.Is.is((String) myStringProp);
      return this;
    }

    public Condition<InspectableComponent> build() {
      final Condition<InspectableComponent> mainBuilder =
          new Condition<InspectableComponent>() {
            @Override
            public boolean matches(InspectableComponent value) {
              if (!value
                  .getComponentClass()
                  .isAssignableFrom(
                      com.facebook.litho.processor.integration.resources.BasicLayout.class)) {
                as(
                    new TextDescription(
                        "Sub-component of type \"com.facebook.litho.processor.integration.resources.BasicLayout\""));
                return false;
              }
              final com.facebook.litho.processor.integration.resources.BasicLayout impl =
                  (com.facebook.litho.processor.integration.resources.BasicLayout)
                      value.getComponent();
              Component propValueChildComponent = null;
              try {
                propValueChildComponent =
                    (Component) impl.getClass().getDeclaredField("child").get(impl);
              } catch (Exception e) {
                // TODO(T25404536): Temporarily ignored.
              }
              if (mChildComponentMatcher != null
                  && !mChildComponentMatcher.matches(
                      value.getNestedInstance(propValueChildComponent))) {
                as(mChildComponentMatcher.description());
                return false;
              }
              Component propValueChild = null;
              try {
                propValueChild = (Component) impl.getClass().getDeclaredField("child").get(impl);
              } catch (Exception e) {
                // TODO(T25404536): Temporarily ignored.
              }
              if (mChildMatcher != null && !mChildMatcher.matches(propValueChild)) {
                as(
                    new TextDescription(
                        "Sub-component of type <BasicLayout> with prop <child> %s (doesn't match %s)",
                        mChildMatcher, propValueChild));
                return false;
              }
              Float propValueMyDimenSizeProp = null;
              try {
                propValueMyDimenSizeProp =
                    (float) impl.getClass().getDeclaredField("myDimenSizeProp").get(impl);
              } catch (Exception e) {
                // TODO(T25404536): Temporarily ignored.
              }
              if (mMyDimenSizePropMatcher != null
                  && !mMyDimenSizePropMatcher.matches(propValueMyDimenSizeProp)) {
                as(
                    new TextDescription(
                        "Sub-component of type <BasicLayout> with prop <myDimenSizeProp> %s (doesn't match %s)",
                        mMyDimenSizePropMatcher, propValueMyDimenSizeProp));
                return false;
              }
              Integer propValueMyRequiredColorProp = null;
              try {
                propValueMyRequiredColorProp =
                    (int) impl.getClass().getDeclaredField("myRequiredColorProp").get(impl);
              } catch (Exception e) {
                // TODO(T25404536): Temporarily ignored.
              }
              if (mMyRequiredColorPropMatcher != null
                  && !mMyRequiredColorPropMatcher.matches(propValueMyRequiredColorProp)) {
                as(
                    new TextDescription(
                        "Sub-component of type <BasicLayout> with prop <myRequiredColorProp> %s (doesn't match %s)",
                        mMyRequiredColorPropMatcher, propValueMyRequiredColorProp));
                return false;
              }
              String propValueMyStringProp = null;
              try {
                propValueMyStringProp =
                    (String) impl.getClass().getDeclaredField("myStringProp").get(impl);
              } catch (Exception e) {
                // TODO(T25404536): Temporarily ignored.
              }
              if (mMyStringPropMatcher != null
                  && !mMyStringPropMatcher.matches(propValueMyStringProp)) {
                as(
                    new TextDescription(
                        "Sub-component of type <BasicLayout> with prop <myStringProp> %s (doesn't match %s)",
                        mMyStringPropMatcher, propValueMyStringProp));
                return false;
              }
              return true;
            }
          };
      return Java6Assertions.allOf(mainBuilder, BaseMatcherBuilder.buildCommonMatcher(this));
    }

    @Override
    public Matcher getThis() {
      return this;
    }
  }
}
