package com.google.eclipse.mechanic.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;

public final class StandardContentProvider implements IUriContentProvider {

  // Keeps an unresponsive server from blocking the Mechanic job indefinitely.
  private static final int TIMEOUT_MILLIS = 30_000;

  public InputStream get(URI uri) throws IOException {
    return openConnection(uri).getInputStream();
  }

  public long lastModifiedTime(URI uri) throws IOException {
    URLConnection connection = openConnection(uri);

    // Using HEAD for Http connections.
    if (connection instanceof HttpURLConnection) {
      ((HttpURLConnection) connection).setRequestMethod("HEAD");
    }

    connection.connect();
    return connection.getLastModified();
  }

  private static URLConnection openConnection(URI uri) throws IOException {
    // Tasks change IDE preferences, so they must not be fetched over an unauthenticated channel.
    if (!"https".equals(uri.getScheme()) && !"file".equals(uri.getScheme())) {
      throw new IOException("Only https: and file: task URLs are supported: " + uri);
    }
    URLConnection connection = uri.toURL().openConnection();
    connection.setConnectTimeout(TIMEOUT_MILLIS);
    connection.setReadTimeout(TIMEOUT_MILLIS);
    return connection;
  }

  public void clear() {
    // Does nothing.
  }
}
