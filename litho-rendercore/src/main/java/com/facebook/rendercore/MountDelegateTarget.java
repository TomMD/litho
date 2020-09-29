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

import androidx.annotation.Nullable;
import com.facebook.rendercore.extensions.MountExtension;
import com.facebook.rendercore.extensions.RenderCoreExtension;
import java.util.ArrayList;

/** Exposes Some RenderCore {@link MountState} API to {@link RenderCoreExtension} */
public interface MountDelegateTarget {

  void notifyMount(
      final MountDelegateInput input, final RenderTreeNode renderTreeNode, final int position);

  void notifyUnmount(int position);

  boolean needsRemount();

  void mount(RenderTree renderTree);

  void attach();

  void detach();

  void unmountAllItems();

  void unbindMountItem(MountItem mountItem);

  boolean isRootItem(int position);

  @Nullable
  MountItem getRootItem();

  Object getContentAt(int position);

  Object getContentById(long id);

  int getContentCount();

  /** @deprecated Only used for Litho's integration. Marked for removal. */
  @Deprecated
  void registerMountDelegateExtension(MountExtension mountExtension);

  ArrayList<Host> getHosts();

  @Nullable
  MountItem getMountItemAt(int position);

  int getMountItemCount();

  void setUnmountDelegateExtension(UnmountDelegateExtension unmountDelegateExtension);
}