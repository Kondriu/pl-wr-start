package org.example.test_tsk;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidateHelper {

    record GetListUsersResponse(int Page, int PerPage, int Total, int TotalPages, List<User> Data) {}
    record User(int Id, String Email, String FirstName, String LastName, String Avatar) {}

    public void ValidateGetUsersResponse(GetListUsersResponse response) {
        assertNotNull(response.Data, "The Data list should not be null.");
        assertEquals(response.PerPage, response.Data.size(), "The 'Data' list should be equal to 'PerPage'. ");

        for (User u : response.Data) {
            assertTrue(u.Id > 0, "Some of Users ID <= 0.");
            assertFalse(u.Email.isEmpty(), "User {user.Id} Email is empty: ");
        }
    }
}
