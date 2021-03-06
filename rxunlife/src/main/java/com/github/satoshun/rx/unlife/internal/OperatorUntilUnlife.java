/**
 * Copyright 2017 Sato Shun.
 * Copyright 2014 Netflix, Inc.
 * <p>
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
package com.github.satoshun.rx.unlife.internal;

import rx.Observable;
import rx.Subscriber;
import rx.observers.SerializedSubscriber;

public final class OperatorUntilUnlife<T, E> implements Observable.Operator<T, T> {

  private final Observable<? extends E> other;

  public OperatorUntilUnlife(final Observable<? extends E> other) {
    this.other = other;
  }

  @Override
  public Subscriber<? super T> call(final Subscriber<? super T> child) {
    final Subscriber<T> serial = new SerializedSubscriber<>(child, false);

    final Subscriber<T> main = new Subscriber<T>(serial, false) {
      @Override
      public void onNext(T t) {
        serial.onNext(t);
      }

      @Override
      public void onError(Throwable e) {
        try {
          serial.onError(e);
        } finally {
          serial.unsubscribe();
        }
      }

      @Override
      public void onCompleted() {
        try {
          serial.onCompleted();
        } finally {
          serial.unsubscribe();
        }
      }
    };

    final Subscriber<E> so = new Subscriber<E>() {
      @Override
      public void onStart() {
        request(Long.MAX_VALUE);
      }

      @Override
      public void onCompleted() {
        serial.unsubscribe();
        child.unsubscribe();
      }

      @Override
      public void onError(Throwable e) {
        serial.unsubscribe();
        child.unsubscribe();
      }

      @Override
      public void onNext(E t) {
        serial.unsubscribe();
        child.unsubscribe();
      }
    };

    serial.add(main);
    serial.add(so);

    child.add(serial);

    other.unsafeSubscribe(so);

    return main;
  }

}
