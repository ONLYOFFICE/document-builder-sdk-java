/**
 * (c) Copyright Ascensio System SIA 2025
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onlyoffice.builder.core;

import docbuilder.CDocBuilderValue;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fluent wrapper around the native CDocBuilderValue that provides a more Java-friendly API for
 * building documents with the ONLYOFFICE Document Builder.
 *
 * <p>This class is designed to be used exclusively through the DocumentSession Fluent API. Direct
 * instantiation is restricted to enforce proper resource management and the Fluent API pattern.
 *
 * <p>This class implements {@link AutoCloseable} to ensure proper resource management of the
 * underlying native resources. It provides methods for:
 *
 * <ul>
 *   <li>Type checking and conversion
 *   <li>Property access and manipulation
 *   <li>Method chaining for fluent operations
 *   <li>Resource management with try-with-resources
 *   <li>Batch operations on multiple resources
 * </ul>
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 * @see DocumentSession
 */
public class DocBuilderValue implements AutoCloseable {
  private static final Logger logger = LoggerFactory.getLogger(DocBuilderValue.class);

  private final CDocBuilderValue delegate;

  /**
   * Package-private constructor for DocBuilderValue.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderValue instances.
   *
   * @param delegate the native CDocBuilderValue to wrap
   */
  DocBuilderValue(CDocBuilderValue delegate) {
    this.delegate = delegate;
  }

  /**
   * Package-private constructor for DocBuilderValue with boolean value.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderValue instances.
   *
   * @param value the boolean value
   */
  DocBuilderValue(boolean value) {
    this.delegate = new CDocBuilderValue(value);
  }

  /**
   * Package-private constructor for DocBuilderValue with integer value.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderValue instances.
   *
   * @param value the integer value
   */
  DocBuilderValue(int value) {
    this.delegate = new CDocBuilderValue(value);
  }

  /**
   * Package-private constructor for DocBuilderValue with double value.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderValue instances.
   *
   * @param value the double value
   */
  DocBuilderValue(double value) {
    this.delegate = new CDocBuilderValue(value);
  }

  /**
   * Package-private constructor for DocBuilderValue with string value.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderValue instances.
   *
   * @param value the string value
   */
  DocBuilderValue(String value) {
    this.delegate = new CDocBuilderValue(value);
  }

  /**
   * Package-private constructor for DocBuilderValue with object array.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderValue instances.
   *
   * @param values the object array
   */
  DocBuilderValue(Object[] values) {
    this.delegate = new CDocBuilderValue(values);
  }

  /**
   * Package-private method to cast an object to a DocBuilderValue.
   *
   * <p>This method is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can use this method.
   *
   * <p>This method supports casting from the following types:
   *
   * <ul>
   *   <li>{@link DocBuilderValue} - returns the object as-is
   *   <li>{@link Boolean} - creates a new DocBuilderValue with the boolean value
   *   <li>{@link Integer} - creates a new DocBuilderValue with the integer value
   *   <li>{@link Double} - creates a new DocBuilderValue with the double value
   *   <li>{@link String} - creates a new DocBuilderValue with the string value
   *   <li>{@code Object[]} - creates a new DocBuilderValue with the array
   * </ul>
   *
   * @param object the object to cast (can be null, which creates an undefined value)
   * @return a DocBuilderValue representing the object
   * @throws IllegalArgumentException if the object type is not supported
   */
  static DocBuilderValue cast(Object object) {
    logger.debug(
        "Casting object of type: {}", object != null ? object.getClass().getSimpleName() : "null");

    if (object instanceof DocBuilderValue) return (DocBuilderValue) object;
    if (object instanceof Boolean) return new DocBuilderValue((Boolean) object);
    if (object instanceof Integer) return new DocBuilderValue((Integer) object);
    if (object instanceof Double) return new DocBuilderValue((Double) object);
    if (object instanceof String) return new DocBuilderValue((String) object);
    if (object instanceof Object[]) return new DocBuilderValue((Object[]) object);

    throw new IllegalArgumentException(
        "Supplied object type is not supported: "
            + (object != null ? object.getClass().getName() : "null"));
  }

  /**
   * Package-private method to create a new DocBuilderValue representing an undefined value.
   *
   * <p>This method is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can use this method.
   *
   * @return a new DocBuilderValue with undefined value
   */
  static DocBuilderValue createUndefined() {
    logger.debug("Creating undefined value");
    return new DocBuilderValue(CDocBuilderValue.createUndefined());
  }

  /**
   * Package-private method to create a new DocBuilderValue representing a null value.
   *
   * <p>This method is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can use this method.
   *
   * @return a new DocBuilderValue with null value
   */
  static DocBuilderValue createNull() {
    logger.debug("Creating null value");
    return new DocBuilderValue(CDocBuilderValue.createNull());
  }

  /**
   * Package-private method to create a new DocBuilderValue representing an array with the specified
   * length.
   *
   * <p>This method is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can use this method.
   *
   * <p>Creates a new DocBuilderValue representing an array with the specified number of elements.
   * The array elements are initially undefined and can be populated using array access methods.
   * This method is useful for creating arrays that will be populated with document content.
   *
   * @param length the length of the array to create (must be non-negative)
   * @return a new DocBuilderValue representing an array
   * @throws IllegalArgumentException if length is negative
   */
  static DocBuilderValue createArray(int length) {
    if (length < 0)
      throw new IllegalArgumentException("Array length cannot be negative: " + length);
    logger.debug("Creating array with length: {}", length);
    return new DocBuilderValue(CDocBuilderValue.createArray(length));
  }

  /**
   * Helper method that logs a method call and returns the result.
   *
   * @param <T> the type of the result
   * @param methodName the name of the method being called
   * @param result the result to return
   * @return the result
   */
  private <T> T logAndReturn(String methodName, T result) {
    logger.debug("{} called, result: {}", methodName, result);
    return result;
  }

  /**
   * Checks if this DocBuilderValue is empty.
   *
   * @return true if the value is empty, false otherwise
   */
  public boolean isEmpty() {
    return logAndReturn("isEmpty()", delegate.isEmpty());
  }

  /**
   * Checks if this DocBuilderValue represents a null value.
   *
   * @return true if the value is null, false otherwise
   */
  public boolean isNull() {
    return logAndReturn("isNull()", delegate.isNull());
  }

  /**
   * Checks if this DocBuilderValue represents an undefined value.
   *
   * @return true if the value is undefined, false otherwise
   */
  public boolean isUndefined() {
    return logAndReturn("isUndefined()", delegate.isUndefined());
  }

  /**
   * Checks if this DocBuilderValue represents a boolean value.
   *
   * @return true if the value is a boolean, false otherwise
   */
  public boolean isBool() {
    return logAndReturn("isBool()", delegate.isBool());
  }

  /**
   * Checks if this DocBuilderValue represents an integer value.
   *
   * @return true if the value is an integer, false otherwise
   */
  public boolean isInt() {
    return logAndReturn("isInt()", delegate.isInt());
  }

  /**
   * Checks if this DocBuilderValue represents a double value.
   *
   * @return true if the value is a double, false otherwise
   */
  public boolean isDouble() {
    return logAndReturn("isDouble()", delegate.isDouble());
  }

  /**
   * Checks if this DocBuilderValue represents a string value.
   *
   * @return true if the value is a string, false otherwise
   */
  public boolean isString() {
    return logAndReturn("isString()", delegate.isString());
  }

  /**
   * Checks if this DocBuilderValue represents a function.
   *
   * @return true if the value is a function, false otherwise
   */
  public boolean isFunction() {
    return logAndReturn("isFunction()", delegate.isFunction());
  }

  /**
   * Checks if this DocBuilderValue represents an object.
   *
   * @return true if the value is an object, false otherwise
   */
  public boolean isObject() {
    return logAndReturn("isObject()", delegate.isObject());
  }

  /**
   * Checks if this DocBuilderValue represents an array.
   *
   * @return true if the value is an array, false otherwise
   */
  public boolean isArray() {
    return logAndReturn("isArray()", delegate.isArray());
  }

  /**
   * Gets the length of this DocBuilderValue if it represents an array.
   *
   * @return the length of the array, or 0 if not an array
   */
  public int getLength() {
    return logAndReturn("getLength()", delegate.getLength());
  }

  /**
   * Converts this DocBuilderValue to a boolean value.
   *
   * @return the boolean representation of this value
   */
  public boolean toBool() {
    return logAndReturn("toBool()", delegate.toBool());
  }

  /**
   * Converts this DocBuilderValue to an integer value.
   *
   * @return the integer representation of this value
   */
  public int toInt() {
    return logAndReturn("toInt()", delegate.toInt());
  }

  /**
   * Converts this DocBuilderValue to a double value.
   *
   * @return the double representation of this value
   */
  public double toDouble() {
    return logAndReturn("toDouble()", delegate.toDouble());
  }

  /**
   * Converts this DocBuilderValue to a string value.
   *
   * @return the string representation of this value
   */
  public String toString() {
    return logAndReturn("toString()", delegate.toString());
  }

  /**
   * Gets a property of this DocBuilderValue by name.
   *
   * @param name the name of the property to get
   * @return a new DocBuilderValue representing the property value
   */
  DocBuilderValue getProperty(String name) {
    logger.debug("getProperty() called with name: {}", name);
    return new DocBuilderValue(delegate.getProperty(name));
  }

  /**
   * Gets a property of this DocBuilderValue by index.
   *
   * @param index the index of the property to get
   * @return a new DocBuilderValue representing the property value
   */
  DocBuilderValue getProperty(int index) {
    logger.debug("getProperty() called with index: {}", index);
    return new DocBuilderValue(delegate.get(index));
  }

  /**
   * Helper method that unwraps a DocBuilderValue to get its underlying delegate, or returns the
   * original value if it's not a DocBuilderValue.
   *
   * @param value the value to unwrap
   * @return the unwrapped value
   */
  private Object unwrapValue(Object value) {
    return (value instanceof DocBuilderValue) ? ((DocBuilderValue) value).delegate : value;
  }

  /**
   * Sets a property of this DocBuilderValue by name.
   *
   * @param name the name of the property to set
   * @param value the value to set (can be a DocBuilderValue or a primitive/object)
   */
  void setProperty(String name, Object value) {
    logger.debug("setProperty() called with name: {}, value: {}", name, value);
    var v = unwrapValue(value);
    delegate.setProperty(name, v);
  }

  /**
   * Sets a property of this DocBuilderValue by index.
   *
   * @param index the index of the property to set
   * @param value the value to set (can be a DocBuilderValue or a primitive/object)
   */
  void setProperty(int index, Object value) {
    logger.debug("setProperty() called with index: {}, value: {}", index, value);
    var v = unwrapValue(value);
    delegate.set(index, v);
  }

  /**
   * Calls a method on this DocBuilderValue with no arguments.
   *
   * @param name the name of the method to call
   * @return a new DocBuilderValue representing the result of the method call
   */
  DocBuilderValue call(String name) {
    logger.debug("call() called with name: {}", name);
    return new DocBuilderValue(delegate.call(name));
  }

  /**
   * Calls a method on this DocBuilderValue with one argument.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @return a new DocBuilderValue representing the result of the method call
   */
  DocBuilderValue call(String name, Object v1) {
    logger.debug("call() called with name: {}, arg1: {}", name, v1);
    var a1 = unwrapValue(v1);
    return new DocBuilderValue(delegate.call(name, a1));
  }

  /**
   * Calls a method on this DocBuilderValue with two arguments.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param v2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @return a new DocBuilderValue representing the result of the method call
   */
  DocBuilderValue call(String name, Object v1, Object v2) {
    logger.debug("call() called with name: {}, arg1: {}, arg2: {}", name, v1, v2);
    var a1 = unwrapValue(v1);
    var a2 = unwrapValue(v2);
    return new DocBuilderValue(delegate.call(name, a1, a2));
  }

  /**
   * Calls a method on this DocBuilderValue with three arguments.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param v2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param v3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @return a new DocBuilderValue representing the result of the method call
   */
  DocBuilderValue call(String name, Object v1, Object v2, Object v3) {
    logger.debug("call() called with name: {}, arg1: {}, arg2: {}, arg3: {}", name, v1, v2, v3);
    var a1 = unwrapValue(v1);
    var a2 = unwrapValue(v2);
    var a3 = unwrapValue(v3);
    return new DocBuilderValue(delegate.call(name, a1, a2, a3));
  }

  /**
   * Calls a method on this DocBuilderValue with four arguments.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param v2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param v3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param v4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @return a new DocBuilderValue representing the result of the method call
   */
  DocBuilderValue call(String name, Object v1, Object v2, Object v3, Object v4) {
    logger.debug(
        "call() called with name: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}",
        name,
        v1,
        v2,
        v3,
        v4);
    var a1 = unwrapValue(v1);
    var a2 = unwrapValue(v2);
    var a3 = unwrapValue(v3);
    var a4 = unwrapValue(v4);
    return new DocBuilderValue(delegate.call(name, a1, a2, a3, a4));
  }

  /**
   * Calls a method on this DocBuilderValue with five arguments.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param v2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param v3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param v4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @param v5 the fifth argument (can be a DocBuilderValue or a primitive/object)
   * @return a new DocBuilderValue representing the result of the method call
   */
  DocBuilderValue call(String name, Object v1, Object v2, Object v3, Object v4, Object v5) {
    logger.debug(
        "call() called with name: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}, arg5: {}",
        name,
        v1,
        v2,
        v3,
        v4,
        v5);
    var a1 = unwrapValue(v1);
    var a2 = unwrapValue(v2);
    var a3 = unwrapValue(v3);
    var a4 = unwrapValue(v4);
    var a5 = unwrapValue(v5);
    return new DocBuilderValue(delegate.call(name, a1, a2, a3, a4, a5));
  }

  /**
   * Calls a method on this DocBuilderValue with six arguments.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param v2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param v3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param v4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @param v5 the fifth argument (can be a DocBuilderValue or a primitive/object)
   * @param v6 the sixth argument (can be a DocBuilderValue or a primitive/object)
   * @return a new DocBuilderValue representing the result of the method call
   */
  DocBuilderValue call(
      String name, Object v1, Object v2, Object v3, Object v4, Object v5, Object v6) {
    logger.debug(
        "call() called with name: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}, arg5: {}, arg6: {}",
        name,
        v1,
        v2,
        v3,
        v4,
        v5,
        v6);
    var a1 = unwrapValue(v1);
    var a2 = unwrapValue(v2);
    var a3 = unwrapValue(v3);
    var a4 = unwrapValue(v4);
    var a5 = unwrapValue(v5);
    var a6 = unwrapValue(v6);
    return new DocBuilderValue(delegate.call(name, a1, a2, a3, a4, a5, a6));
  }

  /**
   * Executes a method call and returns this DocBuilderValue for method chaining. The method is
   * called with no arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param name the name of the method to call
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call fails
   */
  public DocBuilderValue chain(String name) {
    logger.debug("chain() called with name: {}", name);
    try (var ignored = call(name)) {
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute chain call: " + name, e);
    }
  }

  /**
   * Executes a method call and returns this DocBuilderValue for method chaining. The method is
   * called with one argument.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call fails
   */
  public DocBuilderValue chain(String name, Object v1) {
    logger.debug("chain() called with name: {}, arg1: {}", name, v1);
    try (var ignored = call(name, v1)) {
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute chain call: " + name, e);
    }
  }

  /**
   * Executes a method call and returns this DocBuilderValue for method chaining. The method is
   * called with two arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param v2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call fails
   */
  public DocBuilderValue chain(String name, Object v1, Object v2) {
    logger.debug("chain() called with name: {}, arg1: {}, arg2: {}", name, v1, v2);
    try (var ignored = call(name, v1, v2)) {
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute chain call: " + name, e);
    }
  }

  /**
   * Executes a method call and returns this DocBuilderValue for method chaining. The method is
   * called with three arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param v2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param v3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call fails
   */
  public DocBuilderValue chain(String name, Object v1, Object v2, Object v3) {
    logger.debug("chain() called with name: {}, arg1: {}, arg2: {}, arg3: {}", name, v1, v2, v3);
    try (var ignored = call(name, v1, v2, v3)) {
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute chain call: " + name, e);
    }
  }

  /**
   * Executes a method call and returns this DocBuilderValue for method chaining. The method is
   * called with four arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param v2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param v3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param v4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call fails
   */
  public DocBuilderValue chain(String name, Object v1, Object v2, Object v3, Object v4) {
    logger.debug(
        "chain() called with name: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}",
        name,
        v1,
        v2,
        v3,
        v4);
    try (var ignored = call(name, v1, v2, v3, v4)) {
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute chain call: " + name, e);
    }
  }

  /**
   * Executes a method call and returns this DocBuilderValue for method chaining. The method is
   * called with five arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param v2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param v3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param v4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @param v5 the fifth argument (can be a DocBuilderValue or a primitive/object)
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call fails
   */
  public DocBuilderValue chain(String name, Object v1, Object v2, Object v3, Object v4, Object v5) {
    logger.debug(
        "chain() called with name: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}, arg5: {}",
        name,
        v1,
        v2,
        v3,
        v4,
        v5);
    try (var ignored = call(name, v1, v2, v3, v4, v5)) {
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute chain call: " + name, e);
    }
  }

  /**
   * Executes a method call and returns this DocBuilderValue for method chaining. The method is
   * called with six arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param name the name of the method to call
   * @param v1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param v2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param v3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param v4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @param v5 the fifth argument (can be a DocBuilderValue or a primitive/object)
   * @param v6 the sixth argument (can be a DocBuilderValue or a primitive/object)
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call fails
   */
  public DocBuilderValue chain(
      String name, Object v1, Object v2, Object v3, Object v4, Object v5, Object v6) {
    logger.debug(
        "chain() called with name: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}, arg5: {}, arg6: {}",
        name,
        v1,
        v2,
        v3,
        v4,
        v5,
        v6);
    try (var ignored = call(name, v1, v2, v3, v4, v5, v6)) {
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute chain call: " + name, e);
    }
  }

  /**
   * Executes a method call and applies operations to the returned value, then returns this
   * DocBuilderValue. The method is called with no arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param methodName the name of the method to call
   * @param operations a consumer that performs operations on the returned value
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call or operations fail
   */
  public DocBuilderValue with(String methodName, Consumer<DocBuilderValue> operations) {
    logger.debug("with() called with methodName: {}", methodName);
    try (var child = call(methodName)) {
      operations.accept(child);
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute operations on " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies operations to the returned value, then returns this
   * DocBuilderValue. The method is called with one argument.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param operations a consumer that performs operations on the returned value
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call or operations fail
   */
  public DocBuilderValue with(
      String methodName, Object arg1, Consumer<DocBuilderValue> operations) {
    logger.debug("with() called with methodName: {}, arg1: {}", methodName, arg1);
    try (var child = call(methodName, arg1)) {
      operations.accept(child);
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute operations on " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies operations to the returned value, then returns this
   * DocBuilderValue. The method is called with two arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param arg2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param operations a consumer that performs operations on the returned value
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call or operations fail
   */
  public DocBuilderValue with(
      String methodName, Object arg1, Object arg2, Consumer<DocBuilderValue> operations) {
    logger.debug("with() called with methodName: {}, arg1: {}, arg2: {}", methodName, arg1, arg2);
    try (var child = call(methodName, arg1, arg2)) {
      operations.accept(child);
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute operations on " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies operations to the returned value, then returns this
   * DocBuilderValue. The method is called with three arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param arg2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param arg3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param operations a consumer that performs operations on the returned value
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call or operations fail
   */
  public DocBuilderValue with(
      String methodName,
      Object arg1,
      Object arg2,
      Object arg3,
      Consumer<DocBuilderValue> operations) {
    logger.debug(
        "with() called with methodName: {}, arg1: {}, arg2: {}, arg3: {}",
        methodName,
        arg1,
        arg2,
        arg3);
    try (var child = call(methodName, arg1, arg2, arg3)) {
      operations.accept(child);
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute operations on " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies operations to the returned value, then returns this
   * DocBuilderValue. The method is called with four arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param arg2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param arg3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param arg4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @param operations a consumer that performs operations on the returned value
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call or operations fail
   */
  public DocBuilderValue with(
      String methodName,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4,
      Consumer<DocBuilderValue> operations) {
    logger.debug(
        "with() called with methodName: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}",
        methodName,
        arg1,
        arg2,
        arg3,
        arg4);
    try (var child = call(methodName, arg1, arg2, arg3, arg4)) {
      operations.accept(child);
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute operations on " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies operations to the returned value, then returns this
   * DocBuilderValue. The method is called with five arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param arg2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param arg3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param arg4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @param arg5 the fifth argument (can be a DocBuilderValue or a primitive/object)
   * @param operations a consumer that performs operations on the returned value
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call or operations fail
   */
  public DocBuilderValue with(
      String methodName,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4,
      Object arg5,
      Consumer<DocBuilderValue> operations) {
    logger.debug(
        "with() called with methodName: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}, arg5: {}",
        methodName,
        arg1,
        arg2,
        arg3,
        arg4,
        arg5);
    try (var child = call(methodName, arg1, arg2, arg3, arg4, arg5)) {
      operations.accept(child);
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute operations on " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies operations to the returned value, then returns this
   * DocBuilderValue. The method is called with six arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param arg2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param arg3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param arg4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @param arg5 the fifth argument (can be a DocBuilderValue or a primitive/object)
   * @param arg6 the sixth argument (can be a DocBuilderValue or a primitive/object)
   * @param operations a consumer that performs operations on the returned value
   * @return this DocBuilderValue for method chaining
   * @throws RuntimeException if the method call or operations fail
   */
  public DocBuilderValue with(
      String methodName,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4,
      Object arg5,
      Object arg6,
      Consumer<DocBuilderValue> operations) {
    logger.debug(
        "with() called with methodName: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}, arg5: {}, arg6: {}",
        methodName,
        arg1,
        arg2,
        arg3,
        arg4,
        arg5,
        arg6);
    try (var child = call(methodName, arg1, arg2, arg3, arg4, arg5, arg6)) {
      operations.accept(child);
      return this;
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute operations on " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies a computation to the returned value, then returns the
   * computed result. The method is called with no arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param <T> the type of the computed result
   * @param methodName the name of the method to call
   * @param computation a function that computes a result from the returned value
   * @return the computed result
   * @throws RuntimeException if the method call or computation fails
   */
  public <T> T compute(String methodName, Function<DocBuilderValue, T> computation) {
    logger.debug("compute() called with methodName: {}", methodName);
    try (var child = call(methodName)) {
      return computation.apply(child);
    } catch (Exception e) {
      throw new RuntimeException("Failed to compute result from " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies a computation to the returned value, then returns the
   * computed result. The method is called with one argument.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param <T> the type of the computed result
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param computation a function that computes a result from the returned value
   * @return the computed result
   * @throws RuntimeException if the method call or computation fails
   */
  public <T> T compute(String methodName, Object arg1, Function<DocBuilderValue, T> computation) {
    logger.debug("compute() called with methodName: {}, arg1: {}", methodName, arg1);
    try (var child = call(methodName, arg1)) {
      return computation.apply(child);
    } catch (Exception e) {
      throw new RuntimeException("Failed to compute result from " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies a computation to the returned value, then returns the
   * computed result. The method is called with two arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param <T> the type of the computed result
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param arg2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param computation a function that computes a result from the returned value
   * @return the computed result
   * @throws RuntimeException if the method call or computation fails
   */
  public <T> T compute(
      String methodName, Object arg1, Object arg2, Function<DocBuilderValue, T> computation) {
    logger.debug(
        "compute() called with methodName: {}, arg1: {}, arg2: {}", methodName, arg1, arg2);
    try (var child = call(methodName, arg1, arg2)) {
      return computation.apply(child);
    } catch (Exception e) {
      throw new RuntimeException("Failed to compute result from " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies a computation to the returned value, then returns the
   * computed result. The method is called with three arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param <T> the type of the computed result
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param arg2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param arg3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param computation a function that computes a result from the returned value
   * @return the computed result
   * @throws RuntimeException if the method call or computation fails
   */
  public <T> T compute(
      String methodName,
      Object arg1,
      Object arg2,
      Object arg3,
      Function<DocBuilderValue, T> computation) {
    logger.debug(
        "compute() called with methodName: {}, arg1: {}, arg2: {}, arg3: {}",
        methodName,
        arg1,
        arg2,
        arg3);
    try (var child = call(methodName, arg1, arg2, arg3)) {
      return computation.apply(child);
    } catch (Exception e) {
      throw new RuntimeException("Failed to compute result from " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies a computation to the returned value, then returns the
   * computed result. The method is called with four arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param <T> the type of the computed result
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param arg2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param arg3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param arg4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @param computation a function that computes a result from the returned value
   * @return the computed result
   * @throws RuntimeException if the method call or computation fails
   */
  public <T> T compute(
      String methodName,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4,
      Function<DocBuilderValue, T> computation) {
    logger.debug(
        "compute() called with methodName: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}",
        methodName,
        arg1,
        arg2,
        arg3,
        arg4);
    try (var child = call(methodName, arg1, arg2, arg3, arg4)) {
      return computation.apply(child);
    } catch (Exception e) {
      throw new RuntimeException("Failed to compute result from " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies a computation to the returned value, then returns the
   * computed result. The method is called with five arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param <T> the type of the computed result
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param arg2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param arg3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param arg4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @param arg5 the fifth argument (can be a DocBuilderValue or a primitive/object)
   * @param computation a function that computes a result from the returned value
   * @return the computed result
   * @throws RuntimeException if the method call or computation fails
   */
  public <T> T compute(
      String methodName,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4,
      Object arg5,
      Function<DocBuilderValue, T> computation) {
    logger.debug(
        "compute() called with methodName: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}, arg5: {}",
        methodName,
        arg1,
        arg2,
        arg3,
        arg4,
        arg5);
    try (var child = call(methodName, arg1, arg2, arg3, arg4, arg5)) {
      return computation.apply(child);
    } catch (Exception e) {
      throw new RuntimeException("Failed to compute result from " + methodName, e);
    }
  }

  /**
   * Executes a method call and applies a computation to the returned value, then returns the
   * computed result. The method is called with six arguments.
   *
   * <p>This method automatically manages the lifecycle of the returned value using
   * try-with-resources to ensure proper cleanup.
   *
   * @param <T> the type of the computed result
   * @param methodName the name of the method to call
   * @param arg1 the first argument (can be a DocBuilderValue or a primitive/object)
   * @param arg2 the second argument (can be a DocBuilderValue or a primitive/object)
   * @param arg3 the third argument (can be a DocBuilderValue or a primitive/object)
   * @param arg4 the fourth argument (can be a DocBuilderValue or a primitive/object)
   * @param arg5 the fifth argument (can be a DocBuilderValue or a primitive/object)
   * @param arg6 the sixth argument (can be a DocBuilderValue or a primitive/object)
   * @param computation a function that computes a result from the returned value
   * @return the computed result
   * @throws RuntimeException if the method call or computation fails
   */
  public <T> T compute(
      String methodName,
      Object arg1,
      Object arg2,
      Object arg3,
      Object arg4,
      Object arg5,
      Object arg6,
      Function<DocBuilderValue, T> computation) {
    logger.debug(
        "compute() called with methodName: {}, arg1: {}, arg2: {}, arg3: {}, arg4: {}, arg5: {}, arg6: {}",
        methodName,
        arg1,
        arg2,
        arg3,
        arg4,
        arg5,
        arg6);
    try (var child = call(methodName, arg1, arg2, arg3, arg4, arg5, arg6)) {
      return computation.apply(child);
    } catch (Exception e) {
      throw new RuntimeException("Failed to compute result from " + methodName, e);
    }
  }

  /**
   * Adds text to this DocBuilderValue using the "AddText" method.
   *
   * @param text the text to add
   * @return this DocBuilderValue for method chaining
   */
  public DocBuilderValue addText(String text) {
    return chain("AddText", text);
  }

  /**
   * Adds text to this DocBuilderValue using the "AddText" method and applies styling operations to
   * the returned value.
   *
   * @param text the text to add
   * @param styler a consumer that applies styling to the returned value
   * @return this DocBuilderValue for method chaining
   */
  public DocBuilderValue addText(String text, Consumer<DocBuilderValue> styler) {
    return with("AddText", text, styler);
  }

  /**
   * Sets the style of this DocBuilderValue using the "SetStyle" method.
   *
   * @param style the style to set
   * @return this DocBuilderValue for method chaining
   */
  public DocBuilderValue setStyle(DocBuilderValue style) {
    return chain("SetStyle", style);
  }

  /**
   * Sets the form key of this DocBuilderValue using the "SetFormKey" method.
   *
   * @param key the form key to set
   * @return this DocBuilderValue for method chaining
   */
  public DocBuilderValue setFormKey(String key) {
    return chain("SetFormKey", key);
  }

  /**
   * Sets the tip text of this DocBuilderValue using the "SetTipText" method.
   *
   * @param text the tip text to set
   * @return this DocBuilderValue for method chaining
   */
  public DocBuilderValue setTipText(String text) {
    return chain("SetTipText", text);
  }

  /**
   * Sets whether this DocBuilderValue is required using the "SetRequired" method.
   *
   * @param required true if required, false otherwise
   * @return this DocBuilderValue for method chaining
   */
  public DocBuilderValue setRequired(boolean required) {
    return chain("SetRequired", required);
  }

  /**
   * Sets the placeholder text of this DocBuilderValue using the "SetPlaceholderText" method.
   *
   * @param text the placeholder text to set
   * @return this DocBuilderValue for method chaining
   */
  public DocBuilderValue setPlaceholderText(String text) {
    return chain("SetPlaceholderText", text);
  }

  /**
   * Executes multiple method calls and applies operations to each returned value. This method is
   * useful for performing batch operations on multiple resources.
   *
   * <p>All resources are automatically managed and cleaned up after the operations complete,
   * regardless of whether they succeed or fail.
   *
   * @param operations a consumer that configures and executes multiple operations
   * @return this DocBuilderValue for method chaining
   */
  public DocBuilderValue withMultiple(Consumer<MultiResourceBuilder> operations) {
    logger.debug("withMultiple() called");
    var builder = new MultiResourceBuilder(this);
    operations.accept(builder);
    builder.execute();
    return this;
  }

  /**
   * Gets the underlying native CDocBuilderValue delegate.
   *
   * @return the native CDocBuilderValue delegate
   */
  CDocBuilderValue getDelegate() {
    logger.debug("getDelegate() called");
    return this.delegate;
  }

  /** Clears the contents of this DocBuilderValue. */
  public void clear() {
    logger.debug("clear() called");
    delegate.clear();
  }

  /**
   * Closes this DocBuilderValue and releases any associated native resources.
   *
   * @throws Exception if an error occurs during cleanup
   */
  @Override
  public void close() throws Exception {
    logger.debug("close() called");
    delegate.close();
  }

  /**
   * A builder class for managing multiple resource operations.
   *
   * <p>This class allows you to chain multiple method calls and apply operations to each returned
   * value, with automatic resource management.
   *
   * <p>Example usage:
   *
   * <pre>{@code
   * value.withMultiple(builder -> builder
   *   .and("CreateParagraph", p -> p.chain("SetText", "First paragraph"))
   *   .and("CreateParagraph", p -> p.chain("SetText", "Second paragraph"))
   * );
   * }</pre>
   */
  public static class MultiResourceBuilder {
    private final DocBuilderValue parent;
    private final List<ResourceOperation> operations = new ArrayList<>();

    /**
     * Creates a new MultiResourceBuilder for the specified parent DocBuilderValue.
     *
     * @param parent the parent DocBuilderValue
     */
    MultiResourceBuilder(DocBuilderValue parent) {
      this.parent = parent;
      logger.debug("MultiResourceBuilder created for parent");
    }

    /**
     * Adds a method call with no arguments to the list of operations.
     *
     * @param methodName the name of the method to call
     * @param operation a consumer that performs operations on the returned value
     * @return this MultiResourceBuilder for method chaining
     */
    public MultiResourceBuilder and(String methodName, Consumer<DocBuilderValue> operation) {
      logger.debug("MultiResourceBuilder.and() called with methodName: {}", methodName);
      operations.add(new ResourceOperation(methodName, new Object[0], operation));
      return this;
    }

    /**
     * Adds a method call with one argument to the list of operations.
     *
     * @param methodName the name of the method to call
     * @param arg1 the first argument
     * @param operation a consumer that performs operations on the returned value
     * @return this MultiResourceBuilder for method chaining
     */
    public MultiResourceBuilder and(
        String methodName, Object arg1, Consumer<DocBuilderValue> operation) {
      logger.debug(
          "MultiResourceBuilder.and() called with methodName: {}, arg1: {}", methodName, arg1);
      operations.add(new ResourceOperation(methodName, new Object[] {arg1}, operation));
      return this;
    }

    /**
     * Adds a method call with two arguments to the list of operations.
     *
     * @param methodName the name of the method to call
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param operation a consumer that performs operations on the returned value
     * @return this MultiResourceBuilder for method chaining
     */
    public MultiResourceBuilder and(
        String methodName, Object arg1, Object arg2, Consumer<DocBuilderValue> operation) {
      logger.debug(
          "MultiResourceBuilder.and() called with methodName: {}, arg1: {}, arg2: {}",
          methodName,
          arg1,
          arg2);
      operations.add(new ResourceOperation(methodName, new Object[] {arg1, arg2}, operation));
      return this;
    }

    /**
     * Executes all configured operations and applies the specified operations to each returned
     * value. All resources are automatically cleaned up after execution.
     */
    void execute() {
      logger.debug("MultiResourceBuilder.execute() called with {} operations", operations.size());
      var resources = new ArrayList<DocBuilderValue>();
      try {
        for (var op : operations) {
          DocBuilderValue resource;
          if (op.args.length == 0) resource = parent.call(op.methodName);
          else if (op.args.length == 1) resource = parent.call(op.methodName, op.args[0]);
          else if (op.args.length == 2)
            resource = parent.call(op.methodName, op.args[0], op.args[1]);
          else
            throw new IllegalArgumentException("Too many arguments for method: " + op.methodName);
          resources.add(resource);
        }

        for (int i = 0; i < operations.size(); i++)
          operations.get(i).operation.accept(resources.get(i));
      } finally {
        logger.debug("Closing {} resources", resources.size());
        for (var resource : resources) {
          try {
            if (resource != null) resource.close();
          } catch (Exception e) {
            logger.error("Failed to close resource: {}", e.getMessage());
          }
        }
      }
    }

    /** Represents a single resource operation with its method name, arguments, and operation. */
    private static class ResourceOperation {
      final String methodName;
      final Object[] args;
      final Consumer<DocBuilderValue> operation;

      /**
       * Creates a new ResourceOperation.
       *
       * @param methodName the name of the method to call
       * @param args the arguments to pass to the method
       * @param operation the operation to perform on the returned value
       */
      ResourceOperation(String methodName, Object[] args, Consumer<DocBuilderValue> operation) {
        this.methodName = methodName;
        this.args = args;
        this.operation = operation;
      }
    }
  }
}
