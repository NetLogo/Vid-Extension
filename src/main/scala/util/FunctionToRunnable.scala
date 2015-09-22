package org.nlogo.extensions.vid.util

import scala.language.implicitConversions

import java.util.concurrent.Callable

import javafx.beans.value.{ ChangeListener, ObservableValue }

object FunctionToCallback {
  implicit def function2Runnable(f: () => Any): Runnable = {
    new Runnable {
      override def run(): Unit = f()
    }
  }

  implicit def function2Callable[T](f: () => T): Callable[T] = {
    new Callable[T] {
      override def call(): T = f()
    }
  }

  implicit def function2ChangeListener[T](f: (T, T) => Unit): ChangeListener[T] =
    new ChangeListener[T] {
      def changed(obs: ObservableValue[_ <: T], oldVal: T, newVal: T) = f(oldVal, newVal)
    }
}
