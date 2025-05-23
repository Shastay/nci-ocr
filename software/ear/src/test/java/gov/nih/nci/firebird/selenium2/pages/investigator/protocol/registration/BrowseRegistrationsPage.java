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
package gov.nih.nci.firebird.selenium2.pages.investigator.protocol.registration;

import gov.nih.nci.firebird.commons.selenium2.util.WebElementUtils;
import gov.nih.nci.firebird.selenium2.pages.base.AbstractMenuPage;
import gov.nih.nci.firebird.selenium2.pages.base.TableListing;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;

/**
 * content/investigator/browse_registrations.jsp
 */
public class BrowseRegistrationsPage extends AbstractMenuPage<BrowseRegistrationsPage> {

    private static final String REGISTRATION_TABLE_EXPAND_BUTTON_SELECTOR = ".accordionToggle";
    private static final String REGISTRATIONS_TABLE_ID = "registrationsAccordionTable";
    private static final String REGISTRATION_ROWS_SELECTOR = ".accordionHeader";
    private final BrowseRegistrationsPageHelper helper = new BrowseRegistrationsPageHelper(this);

    public BrowseRegistrationsPage(WebDriver driver) {
        super(driver);
    }

    public BrowseRegistrationsPageHelper getHelper() {
        return helper;
    }

    public List<RegistrationListing> getRegistrationListings() {
        List<RegistrationListing> listings = Lists.newArrayList();
        List<WebElement> rows = findElements(By.cssSelector(REGISTRATION_ROWS_SELECTOR));
        for (WebElement row : rows) {
            listings.add(new RegistrationListing(row));
        }
        return listings;
    }

    @Override
    public void assertLoaded() {
        super.assertLoaded();
        assertElementWithIdPresent(REGISTRATIONS_TABLE_ID);
        openAllRegistrationTables();
    }

    private void openAllRegistrationTables() {
        List<WebElement> tableExpandButtons = findElements(By.cssSelector(REGISTRATION_TABLE_EXPAND_BUTTON_SELECTOR));
        for (WebElement tableExpandButton : tableExpandButtons) {
            tableExpandButton.click();
        }
    }

    public class RegistrationListing implements TableListing {

        private static final int PROTOCOL_TITLE_COLUMN = 0;
        private static final int PROTOCOL_NUMBER_COLUMN = 1;
        private static final int SPONSOR_NAME_COLUMN = 2;
        private static final int REGISTRATION_TYPE_COLUMN = 3;
        private static final int REGISTRATION_STATUS_COLUMN = 4;

        private final Long id;
        private final String title;
        private final String protocolId;
        private final String sponsor;
        private final String type;
        private final String status;

        private final WebElement editButton;

        public RegistrationListing(WebElement row) {
            id = Long.valueOf(WebElementUtils.getId(row));
            List<WebElement> cells = row.findElements(By.tagName("div"));
            title = cells.get(PROTOCOL_TITLE_COLUMN).getText();
            protocolId = cells.get(PROTOCOL_NUMBER_COLUMN).getText();
            sponsor = cells.get(SPONSOR_NAME_COLUMN).getText();
            type = cells.get(REGISTRATION_TYPE_COLUMN).getText();
            status = cells.get(REGISTRATION_STATUS_COLUMN).getText();
            editButton = row.findElement(By.tagName("a"));
        }

        @Override
        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getProtocolId() {
            return protocolId;
        }

        public String getSponsor() {
            return sponsor;
        }

        public String getType() {
            return type;
        }

        public String getStatus() {
            return status;
        }

        public RegistrationOverviewTab clickRegistrationLink() {
            editButton.click();
            return RegistrationOverviewTab.FACTORY.create(getDriver());
        }
    }
}
