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
package docbuilder;

/**
 * JNI wrapper class for the native document builder value.
 *
 * <p>This class provides a Java interface to the underlying C++ document builder value through JNI
 * (Java Native Interface). Values represent the fundamental data types in the document builder's
 * scripting environment, including primitives, objects, arrays, and functions.
 *
 * <p><strong>Important:</strong> This class manages native memory resources. Always call {@link
 * #close()} to prevent memory leaks when you're done with a value.
 *
 * <p><strong>Thread Safety:</strong> This class is not thread-safe. Each instance should be used by
 * a single thread.
 *
 * <p><strong>Usage:</strong> Value instances are typically created from a {@link
 * CDocBuilderContext} instance or through various constructors. They provide the building blocks
 * for document manipulation operations and can represent any data type supported by the native
 * library.
 *
 * <p><strong>Data Types:</strong> This class supports the following data types:
 *
 * <ul>
 *   <li>Primitive types: boolean, int, double, string
 *   <li>Special values: null, undefined
 *   <li>Complex types: objects, arrays, functions
 * </ul>
 *
 * @see CDocBuilderContext
 * @see CDocBuilder
 * @version 0.1.0
 * @since 0.1.0
 */
public class CDocBuilderValue {
  /** Native handle to the underlying C++ document builder value instance */
  final long c_internal;

  /**
   * Package-private constructor for creating values from native handles.
   *
   * <p>This constructor is used internally by the library to create value instances from native
   * handles returned by other operations.
   *
   * @param value the native handle to the value
   */
  CDocBuilderValue(long value) {
    this.c_internal = value;
  }

  /**
   * Creates a new empty document builder value.
   *
   * <p>This constructor creates a new native value instance. The value will be undefined until
   * explicitly set to a specific type.
   *
   * @throws RuntimeException if the native value cannot be created
   */
  public CDocBuilderValue() {
    this.c_internal = c_Create();
  }

  /**
   * Creates a copy of an existing document builder value.
   *
   * <p>This constructor creates a new value that is a copy of the provided value. This can be
   * useful for creating independent working copies of values.
   *
   * @param value the value to copy
   * @throws IllegalArgumentException if value is null
   */
  public CDocBuilderValue(CDocBuilderValue value) {
    this.c_internal = c_Copy(value.c_internal);
  }

  /**
   * Creates a document builder value with a boolean value.
   *
   * @param value the boolean value to wrap
   */
  public CDocBuilderValue(boolean value) {
    this.c_internal = c_CreateWithBool(value);
  }

  /**
   * Creates a document builder value with an integer value.
   *
   * @param value the integer value to wrap
   */
  public CDocBuilderValue(int value) {
    this.c_internal = c_CreateWithInt(value);
  }

  /**
   * Creates a document builder value with a double value.
   *
   * @param value the double value to wrap
   */
  public CDocBuilderValue(double value) {
    this.c_internal = c_CreateWithDouble(value);
  }

  /**
   * Creates a document builder value with a string value.
   *
   * @param value the string value to wrap
   * @throws IllegalArgumentException if value is null
   */
  public CDocBuilderValue(String value) {
    this.c_internal = c_CreateWithString(value);
  }

  /**
   * Creates a document builder value as an array from an array of objects.
   *
   * <p>This constructor creates an array value and populates it with the provided objects. Each
   * object is converted to an appropriate document builder value using the {@link
   * #getValueFromObject(Object)} method.
   *
   * @param values the array of objects to convert
   * @throws IllegalArgumentException if values is null or contains unsupported types
   */
  public CDocBuilderValue(Object[] values) {
    var length = values.length;
    this.c_internal = c_CreateArray(length);
    for (int i = 0; i < length; i++) set(i, values[i]);
  }

  /**
   * Checks if this value is empty (has no content).
   *
   * @return true if the value is empty, false otherwise
   */
  public boolean isEmpty() {
    return c_IsEmpty(this.c_internal);
  }

  /**
   * Clears the content of this value, making it empty.
   *
   * <p>After calling this method, the value will be empty and may return to an undefined state
   * depending on the native library's behavior.
   */
  public void clear() {
    c_Clear(this.c_internal);
  }

  /**
   * Checks if this value represents a null value.
   *
   * @return true if the value is null, false otherwise
   */
  public boolean isNull() {
    return c_IsNull(this.c_internal);
  }

  /**
   * Checks if this value represents an undefined value.
   *
   * @return true if the value is undefined, false otherwise
   */
  public boolean isUndefined() {
    return c_IsUndefined(this.c_internal);
  }

  /**
   * Checks if this value represents a boolean value.
   *
   * @return true if the value is a boolean, false otherwise
   */
  public boolean isBool() {
    return c_IsBool(this.c_internal);
  }

  /**
   * Checks if this value represents an integer value.
   *
   * @return true if the value is an integer, false otherwise
   */
  public boolean isInt() {
    return c_IsInt(this.c_internal);
  }

  /**
   * Checks if this value represents a double value.
   *
   * @return true if the value is a double, false otherwise
   */
  public boolean isDouble() {
    return c_IsDouble(this.c_internal);
  }

  /**
   * Checks if this value represents a string value.
   *
   * @return true if the value is a string, false otherwise
   */
  public boolean isString() {
    return c_IsString(this.c_internal);
  }

  /**
   * Checks if this value represents a function.
   *
   * @return true if the value is a function, false otherwise
   */
  public boolean isFunction() {
    return c_IsFunction(this.c_internal);
  }

  /**
   * Checks if this value represents an object.
   *
   * @return true if the value is an object, false otherwise
   */
  public boolean isObject() {
    return c_IsObject(this.c_internal);
  }

  /**
   * Checks if this value represents an array.
   *
   * @return true if the value is an array, false otherwise
   */
  public boolean isArray() {
    return c_IsArray(this.c_internal);
  }

  /**
   * Gets the length of this value if it represents an array.
   *
   * <p>This method should only be called on array values. The behavior for non-array values is
   * undefined.
   *
   * @return the length of the array, or undefined value for non-arrays
   */
  public int getLength() {
    return c_GetLength(this.c_internal);
  }

  /**
   * Converts this value to a boolean.
   *
   * <p>The conversion behavior depends on the native library's implementation. It's recommended to
   * check the type first using {@link #isBool()}.
   *
   * @return the boolean representation of this value
   */
  public boolean toBool() {
    return c_ToBool(this.c_internal);
  }

  /**
   * Converts this value to an integer.
   *
   * <p>The conversion behavior depends on the native library's implementation. It's recommended to
   * check the type first using {@link #isInt()}.
   *
   * @return the integer representation of this value
   */
  public int toInt() {
    return c_ToInt(this.c_internal);
  }

  /**
   * Converts this value to a double.
   *
   * <p>The conversion behavior depends on the native library's implementation. It's recommended to
   * check the type first using {@link #isDouble()}.
   *
   * @return the double representation of this value
   */
  public double toDouble() {
    return c_ToDouble(this.c_internal);
  }

  /**
   * Converts this value to a string.
   *
   * <p>The conversion behavior depends on the native library's implementation. It's recommended to
   * check the type first using {@link #isString()}.
   *
   * @return the string representation of this value
   */
  public String toString() {
    return c_ToString(this.c_internal);
  }

  /**
   * Gets a property from this value if it represents an object.
   *
   * <p>This method should only be called on object values. The behavior for non-object values is
   * undefined.
   *
   * @param name the name of the property to retrieve
   * @return a new {@link CDocBuilderValue} representing the property value
   * @throws IllegalArgumentException if name is null
   */
  public CDocBuilderValue getProperty(String name) {
    return new CDocBuilderValue(c_GetProperty(this.c_internal, name));
  }

  /**
   * Gets an element from this value if it represents an array.
   *
   * <p>This method should only be called on array values. The behavior for non-array values is
   * undefined.
   *
   * @param index the index of the element to retrieve
   * @return a new {@link CDocBuilderValue} representing the element value
   * @throws IndexOutOfBoundsException if index is out of bounds
   */
  public CDocBuilderValue get(int index) {
    return new CDocBuilderValue(c_GetByIndex(this.c_internal, index));
  }

  /**
   * Sets a property on this value if it represents an object.
   *
   * <p>This method should only be called on object values. The behavior for non-object values is
   * undefined. The provided value is automatically converted to an appropriate document builder
   * value.
   *
   * @param name the name of the property to set
   * @param value the value to assign to the property
   * @throws IllegalArgumentException if name is null or value is of unsupported type
   */
  public void setProperty(String name, Object value) {
    var dValue = getValueFromObject(value);
    c_SetProperty(this.c_internal, name, dValue.c_internal);
  }

  /**
   * Sets a property on this value if it represents an object.
   *
   * <p>This is an alias for {@link #setProperty(String, Object)} for convenience.
   *
   * @param name the name of the property to set
   * @param value the value to assign to the property
   * @throws IllegalArgumentException if name is null or value is of unsupported type
   */
  public void set(String name, Object value) {
    setProperty(name, value);
  }

  /**
   * Sets an element in this value if it represents an array.
   *
   * <p>This method should only be called on array values. The behavior for non-array values is
   * undefined. The provided value is automatically converted to an appropriate document builder
   * value.
   *
   * @param index the index of the element to set
   * @param value the value to assign to the element
   * @throws IndexOutOfBoundsException if index is out of bounds
   * @throws IllegalArgumentException if value is of unsupported type
   */
  public void set(int index, Object value) {
    var dValue = getValueFromObject(value);
    c_SetByIndex(this.c_internal, index, dValue.c_internal);
  }

  /**
   * Converts a Java object to a document builder value.
   *
   * <p>This static method supports the following Java types:
   *
   * <ul>
   *   <li>{@link CDocBuilderValue} - returned as-is
   *   <li>{@link Boolean} - converted to boolean value
   *   <li>{@link Integer} - converted to integer value
   *   <li>{@link Double} - converted to double value
   *   <li>{@link String} - converted to string value
   *   <li>{@link Object}[] - converted to array value
   * </ul>
   *
   * @param object the Java object to convert
   * @return a new {@link CDocBuilderValue} representing the converted object
   * @throws IllegalArgumentException if the object type is not supported
   */
  static CDocBuilderValue getValueFromObject(Object object) {
    if (object instanceof CDocBuilderValue) return (CDocBuilderValue) object;
    if (object instanceof Boolean) return new CDocBuilderValue((Boolean) object);
    if (object instanceof Integer) return new CDocBuilderValue((Integer) object);
    if (object instanceof Double) return new CDocBuilderValue((Double) object);
    if (object instanceof String) return new CDocBuilderValue((String) object);
    if (object instanceof Object[]) return new CDocBuilderValue((Object[]) object);
    throw new IllegalArgumentException("Supplied object type is not supported");
  }

  /**
   * Creates a new undefined value.
   *
   * <p>Undefined values represent uninitialized or missing data in the document builder's scripting
   * environment.
   *
   * @return a new {@link CDocBuilderValue} representing an undefined value
   */
  public static CDocBuilderValue createUndefined() {
    return new CDocBuilderValue(c_CreateUndefined());
  }

  /**
   * Creates a new null value.
   *
   * <p>Null values represent the absence of a value in the document builder's scripting
   * environment.
   *
   * @return a new {@link CDocBuilderValue} representing a null value
   */
  public static CDocBuilderValue createNull() {
    return new CDocBuilderValue(c_CreateNull());
  }

  /**
   * Creates a new array value with the specified length.
   *
   * <p>The created array will be empty and can be populated using the {@link #set(int, Object)}
   * method.
   *
   * @param length the initial length of the array
   * @return a new {@link CDocBuilderValue} representing an array
   * @throws IllegalArgumentException if length is negative
   */
  public static CDocBuilderValue createArray(int length) {
    return new CDocBuilderValue(c_CreateArray(length));
  }

  /**
   * Calls a method on this value with no parameters.
   *
   * <p>This method should only be called on object values that have methods. The behavior for
   * non-object values is undefined.
   *
   * @param name the name of the method to call
   * @return a new {@link CDocBuilderValue} representing the method's return value
   * @throws IllegalArgumentException if name is null
   */
  public CDocBuilderValue call(String name) {
    return new CDocBuilderValue(c_Call0(this.c_internal, name));
  }

  /**
   * Calls a method on this value with one parameter.
   *
   * <p>This method should only be called on object values that have methods. The behavior for
   * non-object values is undefined. The provided parameter is automatically converted to an
   * appropriate document builder value.
   *
   * @param name the name of the method to call
   * @param v1 the first parameter
   * @return a new {@link CDocBuilderValue} representing the method's return value
   * @throws IllegalArgumentException if name is null or v1 is of unsupported type
   */
  public CDocBuilderValue call(String name, Object v1) {
    var a1 = getValueFromObject(v1);
    return new CDocBuilderValue(c_Call1(this.c_internal, name, a1.c_internal));
  }

  /**
   * Calls a method on this value with two parameters.
   *
   * <p>This method should only be called on object values that have methods. The behavior for
   * non-object values is undefined. The provided parameters are automatically converted to
   * appropriate document builder values.
   *
   * @param name the name of the method to call
   * @param v1 the first parameter
   * @param v2 the second parameter
   * @return a new {@link CDocBuilderValue} representing the method's return value
   * @throws IllegalArgumentException if name is null or any parameter is of unsupported type
   */
  public CDocBuilderValue call(String name, Object v1, Object v2) {
    var a1 = getValueFromObject(v1);
    var a2 = getValueFromObject(v2);
    return new CDocBuilderValue(c_Call2(this.c_internal, name, a1.c_internal, a2.c_internal));
  }

  /**
   * Calls a method on this value with three parameters.
   *
   * <p>This method should only be called on object values that have methods. The behavior for
   * non-object values is undefined. The provided parameters are automatically converted to
   * appropriate document builder values.
   *
   * @param name the name of the method to call
   * @param v1 the first parameter
   * @param v2 the second parameter
   * @param v3 the third parameter
   * @return a new {@link CDocBuilderValue} representing the method's return value
   * @throws IllegalArgumentException if name is null or any parameter is of unsupported type
   */
  public CDocBuilderValue call(String name, Object v1, Object v2, Object v3) {
    var a1 = getValueFromObject(v1);
    var a2 = getValueFromObject(v2);
    var a3 = getValueFromObject(v3);
    return new CDocBuilderValue(
        c_Call3(this.c_internal, name, a1.c_internal, a2.c_internal, a3.c_internal));
  }

  /**
   * Calls a method on this value with four parameters.
   *
   * <p>This method should only be called on object values that have methods. The behavior for
   * non-object values is undefined. The provided parameters are automatically converted to
   * appropriate document builder values.
   *
   * @param name the name of the method to call
   * @param v1 the first parameter
   * @param v2 the second parameter
   * @param v3 the third parameter
   * @param v4 the fourth parameter
   * @return a new {@link CDocBuilderValue} representing the method's return value
   * @throws IllegalArgumentException if name is null or any parameter is of unsupported type
   */
  public CDocBuilderValue call(String name, Object v1, Object v2, Object v3, Object v4) {
    var a1 = getValueFromObject(v1);
    var a2 = getValueFromObject(v2);
    var a3 = getValueFromObject(v3);
    var a4 = getValueFromObject(v4);
    return new CDocBuilderValue(
        c_Call4(this.c_internal, name, a1.c_internal, a2.c_internal, a3.c_internal, a4.c_internal));
  }

  /**
   * Calls a method on this value with five parameters.
   *
   * <p>This method should only be called on object values that have methods. The behavior for
   * non-object values is undefined. The provided parameters are automatically converted to
   * appropriate document builder values.
   *
   * @param name the name of the method to call
   * @param v1 the first parameter
   * @param v2 the second parameter
   * @param v3 the third parameter
   * @param v4 the fourth parameter
   * @param v5 the fifth parameter
   * @return a new {@link CDocBuilderValue} representing the method's return value
   * @throws IllegalArgumentException if name is null or any parameter is of unsupported type
   */
  public CDocBuilderValue call(String name, Object v1, Object v2, Object v3, Object v4, Object v5) {
    var a1 = getValueFromObject(v1);
    var a2 = getValueFromObject(v2);
    var a3 = getValueFromObject(v3);
    var a4 = getValueFromObject(v4);
    var a5 = getValueFromObject(v5);
    return new CDocBuilderValue(
        c_Call5(
            this.c_internal,
            name,
            a1.c_internal,
            a2.c_internal,
            a3.c_internal,
            a4.c_internal,
            a5.c_internal));
  }

  /**
   * Calls a method on this value with six parameters.
   *
   * <p>This method should only be called on object values that have methods. The behavior for
   * non-object values is undefined. The provided parameters are automatically converted to
   * appropriate document builder values.
   *
   * @param name the name of the method to call
   * @param v1 the first parameter
   * @param v2 the second parameter
   * @param v3 the third parameter
   * @param v4 the fourth parameter
   * @param v5 the fifth parameter
   * @param v6 the sixth parameter
   * @return a new {@link CDocBuilderValue} representing the method's return value
   * @throws IllegalArgumentException if name is null or any parameter is of unsupported type
   */
  public CDocBuilderValue call(
      String name, Object v1, Object v2, Object v3, Object v4, Object v5, Object v6) {
    var a1 = getValueFromObject(v1);
    var a2 = getValueFromObject(v2);
    var a3 = getValueFromObject(v3);
    var a4 = getValueFromObject(v4);
    var a5 = getValueFromObject(v5);
    var a6 = getValueFromObject(v6);
    return new CDocBuilderValue(
        c_Call6(
            this.c_internal,
            name,
            a1.c_internal,
            a2.c_internal,
            a3.c_internal,
            a4.c_internal,
            a5.c_internal,
            a6.c_internal));
  }

  /**
   * Closes this value instance and releases native resources.
   *
   * <p>This method destroys the native value instance and frees associated memory. It should be
   * called when you're done with a value to prevent memory leaks.
   *
   * <p><strong>Important:</strong> Always call this method when you're done with a value to prevent
   * memory leaks from native resources.
   *
   * @throws Exception if an error occurs during cleanup
   */
  public void close() throws Exception {
    c_Destroy(this.c_internal);
  }

  /** Creates a new native value instance */
  private static native long c_Create();

  /** Copies an existing native value instance */
  private static native long c_Copy(long handle);

  /** Destroys a native value instance */
  private static native void c_Destroy(long handle);

  /** Checks if a native value is empty */
  private static native boolean c_IsEmpty(long handle);

  /** Clears the content of a native value */
  private static native void c_Clear(long handle);

  /** Checks if a native value is null */
  private static native boolean c_IsNull(long handle);

  /** Checks if a native value is undefined */
  private static native boolean c_IsUndefined(long handle);

  /** Checks if a native value is a boolean */
  private static native boolean c_IsBool(long handle);

  /** Checks if a native value is an integer */
  private static native boolean c_IsInt(long handle);

  /** Checks if a native value is a double */
  private static native boolean c_IsDouble(long handle);

  /** Checks if a native value is a string */
  private static native boolean c_IsString(long handle);

  /** Checks if a native value is a function */
  private static native boolean c_IsFunction(long handle);

  /** Checks if a native value is an object */
  private static native boolean c_IsObject(long handle);

  /** Checks if a native value is an array */
  private static native boolean c_IsArray(long handle);

  /** Gets the length of a native array value */
  private static native int c_GetLength(long handle);

  /** Converts a native value to boolean */
  private static native boolean c_ToBool(long handle);

  /** Converts a native value to integer */
  private static native int c_ToInt(long handle);

  /** Converts a native value to double */
  private static native double c_ToDouble(long handle);

  /** Converts a native value to string */
  private static native String c_ToString(long handle);

  /** Gets a property from a native object value */
  private static native long c_GetProperty(long handle, String name);

  /** Gets an element from a native array value by index */
  private static native long c_GetByIndex(long handle, int index);

  /** Sets a property on a native object value */
  private static native void c_SetProperty(long handle, String name, long valueHandle);

  /** Sets an element in a native array value by index */
  private static native void c_SetByIndex(long handle, int index, long valueHandle);

  /** Creates a native value with a boolean value */
  private static native long c_CreateWithBool(boolean value);

  /** Creates a native value with an integer value */
  private static native long c_CreateWithInt(int value);

  /** Creates a native value with a double value */
  private static native long c_CreateWithDouble(double value);

  /** Creates a native value with a string value */
  private static native long c_CreateWithString(String value);

  /** Creates a native undefined value */
  private static native long c_CreateUndefined();

  /** Creates a native null value */
  private static native long c_CreateNull();

  /** Creates a native array value with specified length */
  private static native long c_CreateArray(int length);

  /** Calls a method on a native value with no parameters */
  private static native long c_Call0(long handle, String name);

  /** Calls a method on a native value with one parameter */
  private static native long c_Call1(long handle, String name, long v1);

  /** Calls a method on a native value with two parameters */
  private static native long c_Call2(long handle, String name, long v1, long v2);

  /** Calls a method on a native value with three parameters */
  private static native long c_Call3(long handle, String name, long v1, long v2, long v3);

  /** Calls a method on a native value with four parameters */
  private static native long c_Call4(long handle, String name, long v1, long v2, long v3, long v4);

  /** Calls a method on a native value with five parameters */
  private static native long c_Call5(
      long handle, String name, long v1, long v2, long v3, long v4, long v5);

  /** Calls a method on a native value with six parameters */
  private static native long c_Call6(
      long handle, String name, long v1, long v2, long v3, long v4, long v5, long v6);
}
