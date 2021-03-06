/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.satoshun.rx.unlife;

import com.github.satoshun.rx.unlife.internal.Preconditions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import rx.Observable;

public class RxUnLife {

  private RxUnLife() {
    throw new AssertionError("No instances");
  }

  /**
   * Binds the given source to a lifecycle.
   * <p>
   * When the lifecycle event occurs, the source will cease to emit any notifications.
   * <p>
   * Use with {@link Observable#compose(Observable.Transformer)}:
   * {@code source.compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.STOP)).subscribe()}
   *
   * @param lifecycle the lifecycle sequence
   * @param event     the event which should conclude notifications from the source
   * @return a reusable {@link Observable.Transformer} that unsubscribes the source at the specified event
   */
  @Nonnull
  @CheckReturnValue
  public static <T, R> UnLifeTransformer<T> bindUntilEvent(
      @Nonnull final Observable<R> lifecycle,
      @Nonnull final R event) {
    Preconditions.checkNotNull(lifecycle, "lifecycle == null");
    Preconditions.checkNotNull(event, "event == null");

    return new UnlifeObservableTransformer<>(lifecycle, event);
  }

  /**
   * Binds the given source to a lifecycle.
   * <p>
   * Use with {@link Observable#compose(Observable.Transformer)}:
   * {@code source.compose(RxUnLife.bind(lifecycle)).subscribe()}
   * <p>
   * This helper automatically determines (based on the lifecycle sequence itself) when the source
   * should stop emitting items. Note that for this method, it assumes <em>any</em> event
   * emitted by the given lifecycle indicates that the lifecycle is over.
   *
   * @param lifecycle the lifecycle sequence
   * @return a reusable {@link Observable.Transformer} that unsubscribes the source whenever the lifecycle emits
   */
  @Nonnull
  @CheckReturnValue
  public static <T, R> UnLifeTransformer<T> bind(@Nonnull final Observable<R> lifecycle) {
    Preconditions.checkNotNull(lifecycle, "lifecycle == null");

    return new UntilUnlifeObservableTransformer<>(lifecycle);
  }
}
