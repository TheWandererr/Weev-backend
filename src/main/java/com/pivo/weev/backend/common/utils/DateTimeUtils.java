package com.pivo.weev.backend.common.utils;

import static java.util.Optional.ofNullable;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Date;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtils {

  public static LocalDate currentLocalDate() {
    return currentLocalDate(ZoneOffset.UTC);
  }

  public static LocalDate currentLocalDate(ZoneId zoneId) {
    return LocalDate.now(zoneId);
  }

  public static Instant currentInstant() {
    return Instant.now();
  }

  public static LocalDateTime currentLocalDateTime() {
    return currentLocalDateTime(ZoneOffset.UTC);
  }

  public static LocalDateTime currentLocalDateTime(ZoneId targetZone) {
    return LocalDateTime.now(targetZone);
  }

  public static ZonedDateTime currentZonedDateTime() {
    return currentZonedDateTime(ZoneOffset.UTC);
  }

  public static ZonedDateTime currentZonedDateTime(ZoneId targetZone) {
    return toZonedDateTime(currentInstant(), targetZone);
  }

  public static LocalDateTime toLocalDateTime(Instant instant, ZoneId targetZone) {
    return ofNullable(instant).map(value -> LocalDateTime.ofInstant(instant, targetZone)).orElse(null);
  }

  public static LocalDateTime toLocalDateTime(Instant instant) {
    return toLocalDateTime(instant, ZoneOffset.UTC);
  }

  public static LocalDateTime toLocalDateTime(Long utcEpochMilli, ZoneId targetZone) {
    return ofNullable(utcEpochMilli).map(Instant::ofEpochMilli).map(instant -> toLocalDateTime(instant, targetZone)).orElse(null);
  }

  public static LocalDateTime toLocalDateTime(Long epochMilli) {
    return toLocalDateTime(epochMilli, ZoneOffset.UTC);
  }

  public static LocalDateTime toLocalDateTime(Date date, ZoneId targetZone) {
    return ofNullable(date).map(Date::toInstant).map(instant -> toLocalDateTime(instant, targetZone)).orElse(null);
  }

  public static LocalDateTime toLocalDateTime(Date date) {
    return toLocalDateTime(date, ZoneOffset.UTC);
  }

  public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime, ZoneId targetZone) {
    return ofNullable(zonedDateTime).map(dateTime -> dateTime.withZoneSameInstant(targetZone))
                                    .map(ZonedDateTime::toLocalDateTime)
                                    .orElse(null);
  }

  public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
    return toLocalDateTime(zonedDateTime, ZoneOffset.UTC);
  }

  public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, ZoneId dateZone) {
    return ofNullable(localDateTime).map(dateTime -> dateTime.atZone(dateZone)).orElse(null);
  }

  public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
    return toZonedDateTime(localDateTime, ZoneOffset.UTC);
  }

  public static ZonedDateTime toZonedDateTime(Date date, ZoneId targetZone) {
    return ofNullable(date).map(value -> ZonedDateTime.ofInstant(toInstant(date), targetZone)).orElse(null);
  }

  public static ZonedDateTime toZonedDateTime(Date date) {
    return toZonedDateTime(date, ZoneOffset.UTC);
  }

  public static ZonedDateTime toZonedDateTime(Instant instant, ZoneId targetZone) {
    return ofNullable(instant).map(value -> ZonedDateTime.ofInstant(instant, targetZone)).orElse(null);
  }

  public static ZonedDateTime toZonedDateTime(Instant instant) {
    return toZonedDateTime(instant, ZoneOffset.UTC);
  }

  public static ZonedDateTime toZonedDateTime(Long epochMilli, ZoneId epochMilliZone) {
    return ofNullable(epochMilli).map(milli -> ZonedDateTime.ofInstant(toInstant(epochMilli), epochMilliZone))
                                 .orElse(null);
  }

  public static Instant toInstant(LocalDateTime localDateTime, ZoneId targetZone) {
    return ofNullable(localDateTime).map(value -> value.toInstant(targetZone.getRules().getOffset(localDateTime))).orElse(null);
  }

  public static Instant toInstant(LocalDateTime localDateTime) {
    return toInstant(localDateTime, ZoneOffset.UTC);
  }

  public static Instant toInstant(Long epochMilli) {
    return Instant.ofEpochMilli(epochMilli);
  }

  public static Instant toInstant(Date date) {
    return ofNullable(date).map(Date::toInstant).orElse(null);
  }

  public static Date toDate(LocalDateTime localDateTime) {
    return ofNullable(localDateTime)
        .map(Timestamp::valueOf)
        .orElse(null);
  }

  public static Date toDate(ZonedDateTime zonedDateTime) {
    return ofNullable(zonedDateTime)
        .map(ChronoZonedDateTime::toInstant)
        .map(Date::from)
        .orElse(null);
  }

  public static Date toDate(LocalDate localDate) {
    return ofNullable(localDate)
        .map(LocalDate::atStartOfDay)
        .map(DateTimeUtils::toInstant)
        .map(Date::from)
        .orElse(null);
  }

  public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
    return ofNullable(date).map(value -> toLocalDateTime(value, zoneId)).map(LocalDateTime::toLocalDate).orElse(null);
  }

  public static Period dateDiff(LocalDate first, LocalDate second) {
    return Period.between(first, second);
  }
}
