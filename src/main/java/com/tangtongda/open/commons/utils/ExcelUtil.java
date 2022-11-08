package com.tangtongda.open.commons.utils;


import com.tangtongda.open.commons.anotations.ExcelColumn;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Tino_Tang
 * @since 2022/11/7
 **/
public final class ExcelUtil {

  private ExcelUtil() {
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

  private static final String EXCEL2003 = "xls";
  private static final String EXCEL2007 = "xlsx";
  private static final String DEFAULT_SHEET = "Sheet1";
  private static final String ERROR_RESULT = "ERROR";

  /**
   * read excel to entity data list
   *
   * @param cls  class type
   * @param file multipart file
   * @param <T>  object
   * @return object list
   */
  public static <T> List<T> readExcel(Class<T> cls, MultipartFile file) {
    if (null == file) {
      LOGGER.error("target file dose not exist");
      return new ArrayList<>();
    }
    String fileName = file.getOriginalFilename();
    if (null == fileName) {
      LOGGER.error("target file dose not exist");
      return new ArrayList<>();
    }
    if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
      LOGGER.error("The file is not a xls file or xlsx file");
      return new ArrayList<>();
    }
    List<T> dataList = new ArrayList<>();
    Workbook workbook = null;
    try (InputStream is = file.getInputStream()) {
      if (fileName.endsWith(EXCEL2007)) {
        workbook = new XSSFWorkbook(is);
      }
      if (fileName.endsWith(EXCEL2003)) {
        workbook = new HSSFWorkbook(is);
      }
      if (workbook == null) {
        LOGGER.error("The file is not a 2003 or 2007 excel file");
        return new ArrayList<>();
      }
      // 类映射  注解 value-->bean columns
      Map<String, List<Field>> classMap = new HashMap<>();
      List<Field> fields = Stream.of(cls.getDeclaredFields()).collect(Collectors.toList());
      fields.forEach(field -> {
        ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
        if (annotation != null) {
          String value = annotation.value();
          if (StringUtils.isBlank(value)) {
            return; // return起到的作用和continue是相同的 语法
          }
          if (!classMap.containsKey(value)) {
            classMap.put(value, new ArrayList<>());
          }
          ReflectionUtils.makeAccessible(field);
          classMap.get(value).add(field);
        }
      });
      // 索引-->columns
      Map<Integer, List<Field>> reflectionMap = new HashMap<>(16);
      // 默认读取第一个sheet
      Sheet sheet = workbook.getSheetAt(0);

      extracted(cls, dataList, classMap, reflectionMap, sheet);
    } catch (Exception e) {
      LOGGER.error("excel parse error,wps excel must be .xlsx file", e);
      return new ArrayList<>();
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (Exception e) {
          LOGGER.error("parse excel exception!", e);
        }
      }
    }
    return dataList;
  }

  private static <T> void extracted(Class<T> cls,
                                    List<T> dataList,
                                    Map<String, List<Field>> classMap,
                                    Map<Integer, List<Field>> reflectionMap,
                                    Sheet sheet)
          throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    boolean firstRow = true;
    for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      // 首行  提取注解
      if (firstRow) {
        for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
          Cell cell = row.getCell(j);
          String cellValue = getCellValue(cell);
          if (classMap.containsKey(cellValue)) {
            reflectionMap.put(j, classMap.get(cellValue));
          }
        }
        firstRow = false;
      } else {
        // 忽略空白行
        if (row == null) {
          continue;
        }
        T t = cls.getDeclaredConstructor().newInstance();
        // 判断是否为空白行
        boolean allBlank = true;
        for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
          if (reflectionMap.containsKey(j)) {
            Cell cell = row.getCell(j);
            String cellValue = getCellValue(cell);
            if (StringUtils.isNotBlank(cellValue)) {
              allBlank = false;
            }
            List<Field> fieldList = reflectionMap.get(j);
            fieldList.forEach(x -> {
              ReflectionUtils.makeAccessible(x);
              try {
                handleField(t, cellValue, x);
              } catch (Exception e) {
                LOGGER.error("reflect field:{} value:{} exception!", x.getName(), cellValue, e);
              }
            });
          }
          if (!allBlank) {
            dataList.add(t);
          } else {
            LOGGER.warn("row:{} is blank ignore!", i);
          }
        }
      }
    }
  }

  /**
   * handle file set value
   *
   * @param t     object
   * @param value value
   * @param field object field
   * @param <T>   object type
   */
  private static <T> void handleField(T t, String value, Field field)
          throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
    Class<?> type = field.getType();
    if (type == void.class || StringUtils.isBlank(value)) {
      return;
    }
    if (type == Object.class) {
      ReflectionUtils.setField(field, t, value);
      // 数字类型
    } else if (type.getSuperclass() == null || type.getSuperclass() == Number.class) {
      if (type == int.class || type == Integer.class) {
        ReflectionUtils.setField(field, t, NumberUtils.toInt(value));
      } else if (type == long.class || type == Long.class) {
        ReflectionUtils.setField(field, t, NumberUtils.toLong(value));
      } else if (type == byte.class || type == Byte.class) {
        ReflectionUtils.setField(field, t, NumberUtils.toByte(value));
      } else if (type == short.class || type == Short.class) {
        ReflectionUtils.setField(field, t, NumberUtils.toShort(value));
      } else if (type == double.class || type == Double.class) {
        ReflectionUtils.setField(field, t, NumberUtils.toDouble(value));
      } else if (type == float.class || type == Float.class) {
        ReflectionUtils.setField(field, t, NumberUtils.toFloat(value));
      } else if (type == char.class || type == Character.class) {
        ReflectionUtils.setField(field, t, CharUtils.toChar(value));
      } else if (type == boolean.class) {
        ReflectionUtils.setField(field, t, BooleanUtils.toBoolean(value));
      } else if (type == BigDecimal.class) {
        ReflectionUtils.setField(field, t, new BigDecimal(value));
      }
    } else if (type == Boolean.class) {
      ReflectionUtils.setField(field, t, BooleanUtils.toBoolean(value));
    } else if (type == Date.class) {
      //
      ReflectionUtils.setField(field, t, value);
    } else if (type == String.class) {
      ReflectionUtils.setField(field, t, value);
    } else {
      Constructor<?> constructor = type.getConstructor(String.class);
      ReflectionUtils.setField(field, t, constructor.newInstance(value));
    }
  }

  /**
   * write excel split header with data list
   *
   * @param response HttpServletResponse
   * @param dataList data : key->row index,value->row data
   * @param headers  header list
   * @param fileName file name
   */
  public static void writeExcel(@NotNull HttpServletResponse response,
                                @NotNull Map<String, List<String>> dataList,
                                @NotNull List<String> headers,
                                @NotNull String fileName) {
    Workbook wb = getWorkbookWithHeaders(dataList, headers);
    // 浏览器下载excel
    buildExcelDocument(fileName, wb, response);
  }

  /**
   * browser download
   *
   * @param response http response
   * @param dataList data list
   * @param cls      class
   * @param fileName file name
   * @param <T>      object type
   */
  public static <T> void writeExcel(@NotNull HttpServletResponse response,
                                    @NotNull List<T> dataList,
                                    @NotNull Class<T> cls,
                                    @NotNull String fileName) {
    Workbook wb = getWorkbook(dataList, cls);
    buildExcelDocument(fileName, wb, response);
  }

  /**
   * download excel with browser
   *
   * @param fileName file name
   * @param wb       workshop
   * @param response http response
   */
  private static void buildExcelDocument(@NotNull String fileName,
                                         @NotNull Workbook wb,
                                         @NotNull HttpServletResponse response) {
    try {
      response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
      response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
      response.flushBuffer();
      wb.write(response.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * write excel to local path
   *
   * @param dataList data
   * @param cls      data entity class
   * @param path     local path
   * @param <T>      class type
   */
  public static <T> void writeLocalExcel(@NotNull List<T> dataList, @NotNull Class<T> cls, @NotNull String path) {
    Workbook wb = getWorkbook(dataList, cls);
    buildExcelFile(path, wb);
  }

  /**
   * write excel to local path
   *
   * @param dataList data : value->row index,value->row data
   * @param path     local path
   */
  public static void writeLocalExcel(@NotNull Map<String, List<String>> dataList,
                                     @NotNull List<String> headers,
                                     @NotNull String path) {
    Workbook wb = getWorkbookWithHeaders(dataList, headers);
    buildExcelFile(path, wb);
  }

  /**
   * write excel wb to file
   *
   * @param path local path
   * @param wb   excel workbook
   */
  private static void buildExcelFile(String path, Workbook wb) {

    File file = new File(path);
    if (file.exists()) {
      file.deleteOnExit();
    }
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      wb.write(fileOutputStream);
    } catch (Exception e) {
      LOGGER.error("file write error");
    }
  }

  /**
   * get excel workbook by entity data list
   *
   * @param dataList entity data list
   * @param cls      entity class
   * @param <T>      entity class type
   * @return excel workbook
   */
  @NotNull
  private static <T> Workbook getWorkbook(List<T> dataList, Class<T> cls) {
    Field[] fields = cls.getDeclaredFields();
    List<Field> fieldList = Arrays.stream(fields).filter(field -> {
      ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
      if (annotation != null && annotation.col() > 0) {
        ReflectionUtils.makeAccessible(field);
        return true;
      }
      return false;
    }).sorted(Comparator.comparing(field -> {
      int col = 0;
      ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
      if (annotation != null) {
        col = annotation.col();
      }
      return col;
    })).collect(Collectors.toList());
    Workbook wb = new XSSFWorkbook();
    Sheet sheet = wb.createSheet(DEFAULT_SHEET);
    AtomicInteger ai = new AtomicInteger();
    Row row = sheet.createRow(ai.getAndIncrement());
    AtomicInteger aj = new AtomicInteger();
    if (!CollectionUtils.isEmpty(fieldList)) {
      // 写入类注解头部
      fieldList.forEach(field -> {
        ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
        String columnName = StringUtils.EMPTY;
        if (annotation != null) {
          columnName = annotation.value();
        }
        Cell cell = row.createCell(aj.getAndIncrement());

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        Font font = wb.createFont();
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(columnName);
      });
    }
    if (!CollectionUtils.isEmpty(dataList)) {
      dataList.forEach(t -> {
        Row row1 = sheet.createRow(ai.getAndIncrement());
        AtomicInteger atomicInteger = new AtomicInteger();
        fieldList.forEach(field -> {
          Object value = StringUtils.EMPTY;
          try {
            value = field.get(t);
          } catch (Exception e) {
            e.printStackTrace();
          }
          Cell cell = row1.createCell(atomicInteger.getAndIncrement());
          if (value != null) {
            cell.setCellValue(value.toString());
          }
        });
      });
    }
    // freezing current pane
    wb.getSheet(DEFAULT_SHEET).createFreezePane(0, 1, 0, 1);
    return wb;
  }

  /**
   * get workbook by data list and headers
   *
   * @param dataList data list
   * @param headers  header list
   * @return excel workbook
   */
  @NotNull
  private static Workbook getWorkbookWithHeaders(Map<String, List<String>> dataList, List<String> headers) {
    Workbook wb = new XSSFWorkbook();
    Sheet sheet = wb.createSheet(DEFAULT_SHEET);
    AtomicInteger ai = new AtomicInteger();
    Row row = sheet.createRow(ai.getAndIncrement());
    AtomicInteger aj = new AtomicInteger();
    if (!CollectionUtils.isEmpty(headers)) {
      // 写入头部
      headers.forEach(columnName -> {
        Cell cell = row.createCell(aj.getAndIncrement());
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        Font font = wb.createFont();
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(columnName);
      });
    }
    // 写入数据
    if (!CollectionUtils.isEmpty(dataList)) {
      for (Map.Entry<String, List<String>> entry : dataList.entrySet()) {
        // write each row data
        Row row1 = sheet.createRow(ai.getAndIncrement());
        AtomicInteger atomicInteger = new AtomicInteger();
        List<String> values = entry.getValue();
        for (String value : values) {
          Cell cell = row1.createCell(atomicInteger.getAndIncrement());
          if (value != null) {
            cell.setCellValue(value);
          }
        }
      }
    }
    // freezing current pane
    wb.getSheet(DEFAULT_SHEET).createFreezePane(0, 1, 0, 1);
    return wb;
  }

  /**
   * get cell value
   *
   * @param cell excel cell
   * @return value
   */
  private static String getCellValue(Cell cell) {
    if (cell == null) {
      return StringUtils.EMPTY;
    }
    if (cell.getCellType() == CellType.NUMERIC) {
      if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
        return DateUtil.getJavaDate(cell.getNumericCellValue()).toString();
      } else {
        return BigDecimal.valueOf(cell.getNumericCellValue()).toString();
      }
    } else if (cell.getCellType() == CellType.STRING) {
      return StringUtils.trimToEmpty(cell.getStringCellValue());
    } else if (cell.getCellType() == CellType.FORMULA) {
      return StringUtils.trimToEmpty(cell.getCellFormula());
    } else if (cell.getCellType() == CellType.BLANK) {
      return StringUtils.EMPTY;
    } else if (cell.getCellType() == CellType.BOOLEAN) {
      return String.valueOf(cell.getBooleanCellValue());
    } else if (cell.getCellType() == CellType.ERROR) {
      return ERROR_RESULT;
    } else {
      return cell.toString().trim();
    }
  }
}
