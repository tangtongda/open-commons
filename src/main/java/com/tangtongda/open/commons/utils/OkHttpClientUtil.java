package com.tangtongda.open.commons.utils;

import ch.qos.logback.core.util.CloseUtil;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.HttpMethod;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * {@link OkHttpClientUtil}
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2020/12/14
 */
public class OkHttpClientUtil {

  private OkHttpClientUtil() {}

  private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpClientUtil.class);

  private static final String FILE_SPLIT = ".";

  public static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

  public static final MediaType FORM_DATA_MEDIA_TYPE = MediaType.parse("application/from-data");

  /**
   * get a url
   *
   * @param url request url
   * @return response data
   */
  public static String get(@NotEmpty String url) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(url).build();
    try (Response response = client.newCall(request).execute()) {
      if (null == response.body()) {
        return null;
      }
      return response.body().string();
    } catch (IOException e) {
      LOGGER.error("get request error,url:{}", url, e);
    }
    return null;
  }

  /**
   * get a url
   *
   * @param url request url ext: https://google.com/api?id=${id}
   * @return response data
   */
  public static String get(@NotEmpty String url, Map<String, Object> params) {
    if (!CollectionUtils.isEmpty(params)) {
      StringSubstitutor sub = new StringSubstitutor(params);
      url = sub.replace(url);
    }
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(url).build();
    try (Response response = client.newCall(request).execute()) {
      if (null == response.body()) {
        return null;
      }
      return response.body().string();
    } catch (IOException e) {
      LOGGER.error("get request error,url:{}", url, e);
    }
    return null;
  }

  /**
   * post a url
   *
   * @param url request url
   * @param json request json param
   * @return response data
   */
  public static String post(@NotEmpty String url, @NotEmpty String json) {
    OkHttpClient client = new OkHttpClient();
    RequestBody body = RequestBody.create(MEDIA_TYPE, json);
    Request request = new Request.Builder().url(url).post(body).build();
    try (Response response = client.newCall(request).execute()) {
      if (null == response.body()) {
        return null;
      }
      return response.body().string();
    } catch (IOException e) {
      LOGGER.error("post request error,url:{},param:{}", url, json, e);
    }
    return null;
  }

  /**
   * post form data
   *
   * @param url request url
   * @param multipartFile MultipartFile
   * @param file file form data key
   * @param params request params
   * @return response
   */
  public static String postFormData(
      @NotEmpty String url,
      @NotNull MultipartFile multipartFile,
      @NotEmpty String file,
      Map<String, Object> params) {
    // 获取文件名
    String fileName = multipartFile.getOriginalFilename();
    String prefix = StringUtils.EMPTY;
    if (StringUtils.isNotBlank(fileName) && fileName.lastIndexOf(FILE_SPLIT) >= 0) {
      prefix = fileName.substring(fileName.lastIndexOf(FILE_SPLIT));
    }
    // get suffix of file
    File excelFile;
    try {
      // create uuid to avoid repeat temp file
      excelFile = File.createTempFile(IDUtil.uuid(), prefix);
      // MultipartFile to File
      multipartFile.transferTo(excelFile);
    } catch (IOException e) {
      LOGGER.error("temp file create error", e);
      return null;
    }
    OkHttpClient client = new OkHttpClient();
    RequestBody fileBody = RequestBody.create(FORM_DATA_MEDIA_TYPE, excelFile);
    MultipartBody.Builder builder =
        new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(file, fileName, fileBody);
    // build other params
    if (!CollectionUtils.isEmpty(params)) {
      params.forEach((k, value) -> builder.addFormDataPart(k, value.toString()));
    }
    Request request = new Request.Builder().url(url).post(builder.build()).build();
    try (Response response = client.newCall(request).execute()) {
      if (null == response.body()) {
        return null;
      }
      return response.body().string();
    } catch (IOException e) {
      LOGGER.error("post form data request error,url:{},param:{}", url, params, e);
    }
    return null;
  }

  /**
   * get file bytes from server
   *
   * @param requestUrl request url
   * @param json json param
   * @return file byte []
   */
  public static byte[] postFileByte(String requestUrl, String json) {
    PrintWriter printWriter = null;
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      URL url = new URL(requestUrl);
      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
      // post mode
      httpURLConnection.setRequestMethod(HttpMethod.POST);
      // post mode required settings for IO
      httpURLConnection.setDoOutput(true);
      httpURLConnection.setDoInput(true);
      // get URLConnection out put stream
      printWriter = new PrintWriter(httpURLConnection.getOutputStream());
      // write form data param
      if (StringUtils.isNotBlank(json)) printWriter.write(json);
      // flush io
      printWriter.flush();
      // get input stream
      BufferedInputStream bufferedInputStream =
          new BufferedInputStream(httpURLConnection.getInputStream());
      return readBufferedInputStreamToByte(bufferedInputStream);
    } catch (Exception e) {
      LOGGER.error("get wechat file byte form remote http server error", e);
    } finally {
      CloseUtil.closeQuietly(printWriter);
    }
    return new byte[0];
  }

  /**
   * get file InputStream from server
   *
   * @param requestUrl request url
   * @return file InputStream
   */
  public static byte[] getFileByte(String requestUrl) {
    try {
      URL url = new URL(requestUrl);
      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setReadTimeout(5000);
      httpURLConnection.setConnectTimeout(15000);
      httpURLConnection.setRequestMethod(HttpMethod.GET);
      httpURLConnection.connect();
      // 开始获取数据
      BufferedInputStream bufferedInputStream =
          new BufferedInputStream(httpURLConnection.getInputStream());
      return readBufferedInputStreamToByte(bufferedInputStream);
    } catch (Exception e) {
      LOGGER.error("get wechat file byte form remote http server error", e);
    }
    return new byte[0];
  }

  /**
   * read
   *
   * @param bufferedInputStream buffer
   * @return file byte[]
   * @throws IOException io exception
   */
  public static byte[] readBufferedInputStreamToByte(BufferedInputStream bufferedInputStream)
      throws IOException {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len = -1;
    while ((len = bufferedInputStream.read(buffer)) != -1) {
      outStream.write(buffer, 0, len);
    }
    outStream.close();
    bufferedInputStream.close();
    return outStream.toByteArray();
  }
}
