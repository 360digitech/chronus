package com.qihoo.finance.chronus.common.spi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LogoutHandler {

  void logout(HttpServletRequest request, HttpServletResponse response);

}
