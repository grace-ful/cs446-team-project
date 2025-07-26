export function isEnumValue<T>(enumObj: T, value: any): value is T[keyof T] {
  return Object.values(enumObj as object).includes(value);
}