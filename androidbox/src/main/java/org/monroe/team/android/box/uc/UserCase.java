package org.monroe.team.android.box.uc;


public interface UserCase <RequestType,ResponseType> {
    ResponseType execute(RequestType request);
}
