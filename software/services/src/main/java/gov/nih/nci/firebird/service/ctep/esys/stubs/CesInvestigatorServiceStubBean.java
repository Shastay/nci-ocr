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
package gov.nih.nci.firebird.service.ctep.esys.stubs;

import gov.nih.nci.ctep.ces.ocr.api.CesInvestigatorService;
import gov.nih.nci.ctep.ces.ocr.api.Investigator;
import gov.nih.nci.ctep.ces.ocr.api.Organization;
import gov.nih.nci.ctep.ces.ocr.api.Registration;
import gov.nih.nci.firebird.data.AnnualRegistration;
import gov.nih.nci.firebird.data.RegistrationStatus;
import gov.nih.nci.firebird.data.user.FirebirdUser;
import gov.nih.nci.firebird.data.user.InvestigatorStatus;
import gov.nih.nci.firebird.service.user.FirebirdUserService;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Stateless;

import com.google.common.collect.Lists;

/**
 * Stubbed local implementation of ESYS investigator service.
 */
@Stateless
public class CesInvestigatorServiceStubBean implements CesInvestigatorService {

    private FirebirdUserService userService;

    @Resource(mappedName = "firebird/FirebirdUserServiceBean/local")
    void setUserService(FirebirdUserService userService) {
        this.userService = userService;
    }

    @Override
    public Organization getPrimaryOrganization(String investigatorCtepId) {
        return null;
    }

    @Override
    public Investigator getInvestigator(String investigatorCtepId) {
        return null;
    }

    @Override
    public gov.nih.nci.ctep.ces.ocr.api.InvestigatorStatus getInvestigatorStatus(String investigatorCtepId) {
        FirebirdUser investigator = userService.getByCtepId(investigatorCtepId);
        if (InvestigatorStatus.WITHDRAWN == investigator.getInvestigatorRole().getStatus()) {
            return gov.nih.nci.ctep.ces.ocr.api.InvestigatorStatus.WITHDRAWN;
        }
        if (InvestigatorStatus.DISQUALIFIED == investigator.getInvestigatorRole().getStatus()) {
            return gov.nih.nci.ctep.ces.ocr.api.InvestigatorStatus.DISQUALIFIED;
        }
        if (isSuspendedInvestigator(investigator)) {
            return gov.nih.nci.ctep.ces.ocr.api.InvestigatorStatus.SUSPENDED;
        }
        return gov.nih.nci.ctep.ces.ocr.api.InvestigatorStatus.ACTIVE;
    }

    private boolean isSuspendedInvestigator(FirebirdUser user) {
        return user.isCtepUser() && user.isInvestigator() && hasExpiredRegistration(user);
    }

    private boolean hasExpiredRegistration(FirebirdUser investigator) {
        boolean hasApprovedRegistration = false;
        boolean hasCurrentRegistration = false;
        Set<AnnualRegistration> registrations =
                investigator.getInvestigatorRole().getProfile().getAnnualRegistrations();
        for (AnnualRegistration registration : registrations) {
            if (RegistrationStatus.APPROVED == registration.getStatus()) {
                hasApprovedRegistration = true;
                if (registration.getRenewalDate() != null && registration.getRenewalDate().after(new Date())) {
                    hasCurrentRegistration = true;
                    break;
                }
            }
        }
        return hasApprovedRegistration && !hasCurrentRegistration;
    }

    @Override
    public void withdrawInvestigator(String investigatorCtepId, String approverCtepId) {
        // do nothing in this stub
    }

    @Override
    public List<String> getInvestigators() {
        return null;
    }

    @Override
    public List<Registration> getRegistrations(String investigatorCtepId) {
        return null;
    }

    @Override
    public long createRegistration(Investigator investigator, Registration registration) {
        return 0;
    }

    @Override
    public void uploadPacketDocuments(String ctepInvestigatorId, byte[] documents) {
        // do nothing in this stub
    }

    @Override
    public List<String> getUpdatedInvestigators(Date afterStatusUpdateDate) {
        return getSuspendedInvestigatorCtepIds();
    }

    private List<String> getSuspendedInvestigatorCtepIds() {
        List<String> ctepIds = Lists.newArrayList();
        for (FirebirdUser investigator : getSuspendedInvestigators()) {
            ctepIds.add(investigator.getPerson().getCtepId());
        }
        return ctepIds;
    }

    private List<FirebirdUser> getSuspendedInvestigators() {
        List<FirebirdUser> allUsers = userService.getAll();
        List<FirebirdUser> suspendedInvestigators = Lists.newArrayList();
        for (FirebirdUser user : allUsers) {
            if (isSuspendedInvestigator(user)) {
                suspendedInvestigators.add(user);
            }
        }
        return suspendedInvestigators;
    }

}
