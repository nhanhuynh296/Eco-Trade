/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package org.seng302.main.repository;

import org.seng302.main.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * Repository interfaces are used to declare accessors to JPA objects.
 * <p>
 * Spring will scan the project files for its own annotations and perform some
 * startup operations (e.g., instantiating classes).
 * <p>
 * By declaring a "repository rest resource", we can expose repository (JPA)
 * objects through REST API calls. However, This is discouraged in a
 * Model-View-Controller (or similar patterns).
 * Is there a better way of doing this that doesn't sacrifice MVC?
 * <p>
 * See https://docs.spring.io/spring-data/rest/docs/current/reference/html/
 */
@Repository
public
interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Sets the session ticket for a user logging in using their unique email
     *
     * @param sessionId of user
     * @param email of user
     */
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update User u set u.sessionTicket = ?1 where u.email = ?2")
    void setSessionIdByEmail(String sessionId, String email);

    /**
     * Find User from database by id - no password
     *
     * @param userId of user
     * @return List of users that meet the credentials (should only be one)
     */
    @Query("SELECT u FROM User u WHERE u.id = ?1")
    User findUserById(Long userId);

    /**
     * @param email Email of user to check
     */
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findUserByEmail(String email);

    /**
     * Retrieves the user with this session ticket, vulnerable to duplicate tickets
     *
     * @param sessionTicket of the logged-in user to retrieve
     */
    @Query("SELECT u FROM User u where u.sessionTicket = ?1")
    User findUserBySessionTicket(String sessionTicket);

    /**
     * @return An optional user
     */
    @Query("SELECT u from User u WHERE u.email = ?1")
    Optional<User> findByEmail(String email);

    /**
     * Find user by role
     *
     * @param userRole role to search by
     * @return A user object or null
     */
    @Query("SELECT u FROM User u WHERE u.userRole = ?1")
    List<User> findByUserRole(String userRole);

    /**
     * Deletes a user by their role. Currently only used for testing DGAA.
     *
     * @param userRole role to delete by
     */
    void deleteByUserRole(String userRole);

    /**
     * Updates a user's role to admin
     *
     * @param id User to be modified
     */
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.userRole = 'ROLE_ADMIN' WHERE u.id = ?1")
    void updateUserToAdmin(long id);

    /**
     * Updates a user's role to normal user
     *
     * @param id User to be modified
     */
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.userRole = 'ROLE_USER' WHERE u.id = ?1")
    void updateUserToRoleUser(long id);

    /**
     * Updates a user's primary image id
     *
     * @param id User to be modified
     * @param imageId to update the primaryImageId with
     */
    @javax.transaction.Transactional
    @Modifying(flushAutomatically = true)
    @Query("UPDATE User u SET u.primaryImageId = ?2 WHERE u.id = ?1")
    void updateUserPrimaryImage(Long id, Long imageId);
}
