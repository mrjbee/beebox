package org.monroe.team.corebox.uc;


public interface UserCase <RequestType,ResponseType> {
    ResponseType execute(RequestType request);
}
