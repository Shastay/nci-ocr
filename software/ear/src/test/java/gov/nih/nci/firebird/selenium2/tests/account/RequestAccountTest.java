/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The NCI OCR
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This NCI OCR Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the NCI OCR Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the NCI OCR Software; (ii) distribute and
 * have distributed to and by third parties the NCI OCR Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.firebird.selenium2.tests.account;

import static org.junit.Assert.*;
import gov.nih.nci.firebird.common.FirebirdConstants;
import gov.nih.nci.firebird.commons.selenium2.util.WaitUtils;
import gov.nih.nci.firebird.data.Organization;
import gov.nih.nci.firebird.data.user.UserRoleType;
import gov.nih.nci.firebird.selenium2.framework.AbstractFirebirdWebDriverTest;
import gov.nih.nci.firebird.selenium2.pages.login.LoginPage;
import gov.nih.nci.firebird.selenium2.pages.login.RequestAccountPage;
import gov.nih.nci.firebird.selenium2.pages.util.ExpectedValidationFailure;
import gov.nih.nci.firebird.selenium2.pages.util.ExpectedValidationFailure.FailingAction;
import gov.nih.nci.firebird.selenium2.pages.util.FirebirdEmailUtils;
import gov.nih.nci.firebird.service.account.AccountConfigurationData;
import gov.nih.nci.firebird.service.account.AccountConfigurationDataFactory;
import gov.nih.nci.firebird.service.messages.FirebirdMessageTemplate;
import gov.nih.nci.firebird.service.messages.FirebirdStringTemplate;
import gov.nih.nci.firebird.test.LoginAccount;
import gov.nih.nci.firebird.test.util.FirebirdPropertyUtils;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class RequestAccountTest extends AbstractFirebirdWebDriverTest {

    private static final String[] REQUIRED_FIELDS_INTERNATIONAL = new String[] { "account.firstName",
            "account.lastName", "account.emailAddress", "account.phoneNumber", "account.organization",
            "account.postalAddress.streetAddress", "account.postalAddress.city", "account.postalAddress.postalCode" };

    private static final String[] REQUIRED_FIELDS_US = (String[]) ArrayUtils.add(REQUIRED_FIELDS_INTERNATIONAL,
            "account.stateCode");

    private EnumSet<UserRoleType> roles = EnumSet.of(UserRoleType.INVESTIGATOR, UserRoleType.REGISTRATION_COORDINATOR);
    private Collection<Organization> sponsorOrganizations;
    private Organization sponsorOrganization;
    private Organization delegateOrganization;

    @Inject
    @Named("firebird.email.support.address")
    private String supportEmailAddress;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        sponsorOrganizations = getGridResources().getTestDataSource().getProtocolSponsorOrganizations();
        Iterator<Organization> sponsorIterator = sponsorOrganizations.iterator();
        sponsorOrganization = sponsorIterator.next();
        delegateOrganization = sponsorIterator.next();
    }

    @Test
    public void testRequestAccountWithUsAddress() {
        AccountConfigurationData account = getAccountData();
        doRequestAccount(account, REQUIRED_FIELDS_US);
    }

    private AccountConfigurationData getAccountData() {
        AccountConfigurationData account = AccountConfigurationDataFactory.getInstance().create();
        account.setRoles(roles);
        account.getSponsorOrganizations().add(sponsorOrganization);
        account.getDelegateOrganizations().add(delegateOrganization);
        return account;
    }

    private void doRequestAccount(AccountConfigurationData account, String[] expectedRequiredFields) {
        LoginPage loginPage = openLoginPage();
        final RequestAccountPage requestAccountPage = loginPage.clickRequestAccount();
        assertEquals(FirebirdConstants.US_COUNTRY_CODE, requestAccountPage.getCountry());
        requestAccountPage.selectCountry(account.getPerson().getPostalAddress().getCountry());
        checkForRequiredFieldValidations(requestAccountPage, expectedRequiredFields);
        requestAccountPage.getHelper().enterAccountData(account);

        ExpectedValidationFailure expectedValidationFailure = new ExpectedValidationFailure(
                "user.registration.role.selection.error");
        expectedValidationFailure.assertFailureOccurs(new FailingAction() {
            @Override
            public void perform() {
                requestAccountPage.clickRequestAccount();
            }
        });
        requestAccountPage.getHelper().selectRoles(account);
        requestAccountPage.clickRequestAccount();
        checkForExpectedEmails(account);
    }

    private void checkForRequiredFieldValidations(final RequestAccountPage requestAccountPage,
            String[] requiredFieldsExpected) {
        ExpectedValidationFailure expectedValidationFailure = new ExpectedValidationFailure();
        expectedValidationFailure.addExpectedRequiredFields(requiredFieldsExpected);
        expectedValidationFailure.assertFailureOccurs(new FailingAction() {
            @Override
            public void perform() {
                requestAccountPage.clickRequestAccount();
            }
        });
    }

    private void checkForExpectedEmails(AccountConfigurationData account) {
        assertEquals(2, getEmailChecker().getSentEmailSize());
        checkUserEmail(account);
        checkAppSupportEmail(account);
    }

    private void checkUserEmail(AccountConfigurationData account) {
        String expectedSubject = FirebirdEmailUtils
                .getExpectedSubject(FirebirdMessageTemplate.ACCOUNT_REQUEST_USER_NOTIFICATION_EMAIL);
        MimeMessage userEmail = getEmailChecker().getSentEmail(account.getPerson().getEmail(), expectedSubject);
        checkForAccountInformation(userEmail, account);
    }

    private void checkAppSupportEmail(AccountConfigurationData account) {
        String expectedSubject = getExpectedAppSupportEmailSubject(account);
        MimeMessage appSupportEmail = getEmailChecker().getSentEmail(supportEmailAddress, expectedSubject);
        checkForAccountInformation(appSupportEmail, account);
    }

    private String getExpectedAppSupportEmailSubject(AccountConfigurationData account) {
        if (account.isExistingLdapAccount()) {
            return FirebirdPropertyUtils.evaluateAndReturnVelocityProperty(
                    FirebirdStringTemplate.ACCOUNT_REQUEST_EXISTING_LDAP_ACCOUNT_EMAIL_SUBJECT, account);
        } else {
            return FirebirdPropertyUtils.evaluateAndReturnVelocityProperty(
                    FirebirdStringTemplate.ACCOUNT_REQUEST_EMAIL_SUBJECT, account);
        }
    }

    private void checkForAccountInformation(MimeMessage message, AccountConfigurationData account) {
        String body = getEmailChecker().getContent(message);
        if (!StringUtils.isEmpty(account.getUsername())) {
            assertTrue(body.contains(account.getUsername()));
        }
        assertTrue(body.contains(account.getPerson().getFirstName()));
        assertTrue(body.contains(account.getPerson().getLastName()));
        assertTrue(body.contains(account.getPerson().getPhoneNumber()));
        assertTrue(body.contains(account.getPerson().getEmail()));
        assertTrue(body.contains(account.getPrimaryOrganization().getOrganization().getName()));
        assertTrue(body.contains(account.getPerson().getPostalAddress().getStreetAddress()));
        assertTrue(body.contains(account.getPerson().getPostalAddress().getDeliveryAddress()));
        assertTrue(body.contains(account.getPerson().getPostalAddress().getCity()));
        assertTrue(body.contains(account.getPerson().getPostalAddress().getStateOrProvince()));
        assertTrue(body.contains(account.getPerson().getPostalAddress().getPostalCode()));
        assertTrue(body.contains(account.getPerson().getPostalAddress().getCountry()));
        checkEmailForRoles(account.getRoles(), body);
        checkEmailForSponsors(body, account.getSponsorOrganizations());
        checkEmailForSponsors(body, account.getDelegateOrganizations());
    }

    private void checkEmailForRoles(Set<UserRoleType> roles, String body) {
        for (UserRoleType role : roles) {
            assertTrue(body.contains(role.getDisplay()));
        }
    }

    private void checkEmailForSponsors(String body, Set<Organization> sponsorOrganizations) {
        for (Organization sponsor : sponsorOrganizations) {
            assertTrue("Actual Email body did not contain " + sponsor.getName() + ":\n" + body,
                    body.contains(sponsor.getName()));
        }
    }

    @Test
    public void testRequestAccount_ExistingLdapAccount() {
        AccountConfigurationData account = getAccountData();
        account.setExistingLdapAccount(true);
        LoginPage loginPage = openLoginPage();
        RequestAccountPage requestAccountPage = loginPage.clickRequestAccount();
        requestAccountPage.getHelper().enterAccountData(account);
        requestAccountPage.getHelper().selectRoles(account);
        requestAccountPage.getHelper().setHasLdapAccount(true);
        checkEmptyCredentials(requestAccountPage);
        checkInvalidCredentials(requestAccountPage);
        requestAccountPage.getHelper().setCredentials(LoginAccount.InvestigatorLogin.fbciinv4);
        requestAccountPage.clickRequestAccount();
        checkForExpectedEmails(account);
    }

    private void checkEmptyCredentials(final RequestAccountPage requestAccountPage) {
        ExpectedValidationFailure expectedValidationFailure = new ExpectedValidationFailure("username.required");
        expectedValidationFailure.addExpectedMessage("password.required");
        expectedValidationFailure.assertFailureOccurs(new FailingAction() {
            @Override
            public void perform() {
                requestAccountPage.clickRequestAccount();
            }
        });
    }

    private void checkInvalidCredentials(final RequestAccountPage requestAccountPage) {
        requestAccountPage.typeUsername("invalid");
        requestAccountPage.typePassword("credentials");
        ExpectedValidationFailure expectedValidationFailure = new ExpectedValidationFailure(
                "authentication.invalid.credentials");
        expectedValidationFailure.assertFailureOccurs(new FailingAction() {
            @Override
            public void perform() {
                requestAccountPage.clickRequestAccount();
            }
        });
    }

    @Test
    public void testRequestAccount_EmailFail() {
        ignoreLoggedWarnings();
        ignoreLoggedErrors();
        getEmailChecker().stop();
        AccountConfigurationData account = getAccountData();
        LoginPage loginPage = openLoginPage();
        RequestAccountPage requestAccountPage = loginPage.clickRequestAccount();
        requestAccountPage.getHelper().enterAccountData(account);
        requestAccountPage.getHelper().selectRoles(account);
        requestAccountPage.clickRequestAccount();
        WaitUtils.pause(1000);
        getEmailChecker().start();
        assertEquals(0, getEmailChecker().getSentEmailSize());
        WaitUtils.pause(60000);
        assertEquals(2, getEmailChecker().getSentEmailSize());
        checkForExpectedEmails(account);
    }

}
