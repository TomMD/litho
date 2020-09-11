/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.rendercore;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import com.facebook.rendercore.Node.LayoutResult;
import com.facebook.rendercore.extensions.LayoutResultVisitor;
import com.facebook.rendercore.extensions.RenderCoreExtension;
import java.util.ArrayList;
import java.util.Map;

/**
 * Reduces a tree of Node into a flattened tree of RenderTreeNode. As part of the reduction process
 * all the positions are translated relative to the new hosts.
 */
public class Reducer {

  public static final RenderUnit sRootHostRenderUnit =
      new RenderUnit(RenderUnit.RenderType.VIEW) {
        @Override
        public Object createContent(Context c) {
          return null;
        }

        @Override
        public long getId() {
          return MountState.ROOT_HOST_ID;
        }
      };

  private static void reduceTree(
      final Context context,
      final LayoutResult<?> layoutResult,
      final RenderTreeNode parent,
      final int x,
      final int y,
      final ArrayList<RenderTreeNode> flattenedTree,
      final @Nullable Map<RenderCoreExtension<?>, Object> extensions) {

    // If width & height are 0 then do not return the tree.
    if (layoutResult.getWidth() == 0 && layoutResult.getHeight() == 0) {
      return;
    }

    final Rect bounds = new Rect(x, y, x + layoutResult.getWidth(), y + layoutResult.getHeight());
    final int absoluteX = parent.getAbsoluteX() + x;
    final int absoluteY = parent.getAbsoluteY() + y;

    if (extensions != null) {
      for (Map.Entry<RenderCoreExtension<?>, Object> entry : extensions.entrySet()) {
        final RenderCoreExtension<?> e = entry.getKey();
        final LayoutResultVisitor visitor = e.getLayoutVisitor();
        if (visitor != null) {
          final Object state = entry.getValue();
          visitor.visit(layoutResult, bounds, absoluteX, absoluteY, state);
        }
      }
    }

    final RenderUnit renderUnit = layoutResult.getRenderUnit();

    final int xTranslation;
    final int yTranslation;

    final RenderTreeNode nextParent;

    if (renderUnit != null && layoutResult.getChildrenCount() > 0) { // The render unit is a host

      // Create new host node;
      RenderTreeNode node = createRenderTreeNode(layoutResult, renderUnit, parent, x, y);
      flattenedTree.add(node);

      // Add new child to the parent.
      parent.child(node);

      // Set it as the parent for its children.
      nextParent = node;

      // The child do not need to be translated.
      xTranslation = 0;
      yTranslation = 0;

    } else if (renderUnit != null) { // The render unit is a leaf.

      // Create new content node;
      RenderTreeNode content = createRenderTreeNode(layoutResult, renderUnit, parent, x, y);
      flattenedTree.add(content);

      // Add new child to the parent.
      parent.child(content);

      // The next parent is irrelevant because there are not children.
      nextParent = parent;

      // The translations are irrelevant because there are not children.
      xTranslation = 0;
      yTranslation = 0;

    } else { // No render unit.

      // The next parent for any children will be the current parent.
      nextParent = parent;

      // The translations for any children will be inherited from the parent.
      xTranslation = x;
      yTranslation = y;
    }

    for (int i = 0; i < layoutResult.getChildrenCount(); i++) {
      reduceTree(
          context,
          layoutResult.getChildAt(i),
          nextParent,
          layoutResult.getXForChildAtIndex(i) + xTranslation,
          layoutResult.getYForChildAtIndex(i) + yTranslation,
          flattenedTree,
          extensions);
    }
  }

  private static RenderTreeNode createRenderTreeNode(
      Node.LayoutResult layoutResult,
      @Nullable RenderUnit renderUnit,
      @Nullable RenderTreeNode parent,
      int x,
      int y) {

    final boolean hasPadding =
        layoutResult.getPaddingLeft() != 0
            || layoutResult.getPaddingTop() != 0
            || layoutResult.getPaddingRight() != 0
            || layoutResult.getPaddingBottom() != 0;
    final Rect padding =
        hasPadding
            ? new Rect(
                layoutResult.getPaddingLeft(),
                layoutResult.getPaddingTop(),
                layoutResult.getPaddingRight(),
                layoutResult.getPaddingBottom())
            : null;

    RenderTreeNode renderTreeNode =
        new RenderTreeNode(
            parent,
            renderUnit,
            layoutResult.getLayoutData(),
            new Rect(x, y, x + layoutResult.getWidth(), y + layoutResult.getHeight()),
            padding,
            parent != null ? parent.getChildrenCount() : 0);

    return renderTreeNode;
  }

  public static RenderTree getReducedTree(
      final Context context,
      final LayoutResult<?> layoutResult,
      final int widthSpec,
      final int heightSpec,
      final @Nullable RenderCoreExtension<?>[] extensions) {

    final ArrayList<RenderTreeNode> flattenedTree = new ArrayList<>();
    final Map<RenderCoreExtension<?>, Object> results = populate(extensions);

    RenderTreeNode rootHostNode =
        createRenderTreeNode(layoutResult, sRootHostRenderUnit, null, 0, 0);
    flattenedTree.add(rootHostNode);
    reduceTree(context, layoutResult, rootHostNode, 0, 0, flattenedTree, results);
    RenderTreeNode[] trimmedRenderNodeTree =
        flattenedTree.toArray(new RenderTreeNode[flattenedTree.size()]);

    return new RenderTree(rootHostNode, trimmedRenderNodeTree, widthSpec, heightSpec, results);
  }

  private static @Nullable Map<RenderCoreExtension<?>, Object> populate(
      final @Nullable RenderCoreExtension<?>[] extensions) {
    if (extensions == null || extensions.length == 0) {
      return null;
    }

    final Map<RenderCoreExtension<?>, Object> results = new ArrayMap<>(extensions.length);
    for (int i = 0; i < extensions.length; i++) {
      final LayoutResultVisitor<?> visitor = extensions[i].getLayoutVisitor();
      if (visitor != null) {
        final Object state = extensions[i].createState();
        results.put(extensions[i], state);
      }
    }

    return results;
  }
}
