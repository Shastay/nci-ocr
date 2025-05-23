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
package gov.nih.nci.firebird.web.action.investigator.profile;

import gov.nih.nci.firebird.data.CertifyingBoard;
import gov.nih.nci.firebird.data.FormTypeEnum;
import gov.nih.nci.firebird.data.Specialty;
import gov.nih.nci.firebird.exception.ValidationException;
import gov.nih.nci.firebird.service.GenericDataRetrievalService;
import gov.nih.nci.firebird.service.investigatorprofile.InvestigatorProfileService;
import gov.nih.nci.firebird.service.lookup.CountryLookupService;
import gov.nih.nci.firebird.service.lookup.StateLookupService;
import gov.nih.nci.firebird.web.common.FirebirdUIConstants;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Abstract action class to handle medical training credentials creation, deletion, and editing.
 * @param <BOARD> certifying board type
 * @param <SPECIALTY> specialty type
 */
@SuppressWarnings({ "PMD.GenericsNaming", "ucd" })
// provides clarity
// methods called from JSP pages
public abstract class AbstractManageMedicalTrainingCredentialsAction
       <BOARD extends CertifyingBoard<SPECIALTY>, SPECIALTY extends Specialty<BOARD>>
        extends AbstractManageCredentialsAction {

    private static final long serialVersionUID = 1L;

    private BOARD certifyingBoard;
    private Map<String, List<SPECIALTY>> specialtiesJson;
    private final List<SPECIALTY> emptyList = Collections.emptyList();

    /**
     * @param profileService the profileService to set
     * @param stateLookup the stateLookupService to set
     * @param countryLookup country lookup service.
     * @param dataService the GenericDataRetrievalService to set
     */
    protected AbstractManageMedicalTrainingCredentialsAction(InvestigatorProfileService profileService,
            GenericDataRetrievalService dataService, StateLookupService stateLookup,
            CountryLookupService countryLookup) {
        super(profileService, dataService, stateLookup, countryLookup);
    }

    @Override
    public void prepare() {
        if (getCredential() == null) {
            createCredential();
        }
        super.prepare();
        if (getSpecialty() != null) {
            setCertifyingBoard(getSpecialty().getCertifyingBoard());
        }
    }

    /**
     * Creates a new credential.
     */
    protected abstract void createCredential();

    /**
     * Using the credentialType parameter as a decider, this method will route traffic to the correct jsp page. If
     * provided an invalid credential type it will route to the close dialog jsp.
     *
     * @return the struts forward.
     */
    protected String manageCredentialsAjaxEnter() {
        if (getPage() == null || FirebirdUIConstants.RETURN_SEARCH_PAGE.equals(getPage())) {
            setPage(FirebirdUIConstants.RETURN_SEARCH_PAGE);
            setIssuingOrganizationExternalId(null);
        }

        return getPage();
    }

    /**
     * Save the new medical training credential.
     *
     * @return the struts forward
     * @throws ParseException when date format is incorrect
     */
    protected String saveMedicalTrainingCredential() throws ParseException {
        getCredential().setEffectiveDate(getParsedEffectiveDate());
        getCredential().setExpirationDate(getParsedExpirationDate());
        if (!isExpirationAfterEffectiveDates()) {
            return INPUT;
        }
        try {
            return saveCredential();
        } catch (ValidationException e) {
            return handleValidationException(e, getIssuerResource());
        }
    }

    private boolean isExpirationAfterEffectiveDates() {
        try {
            validateExpirationAfterEffectiveDates();
        } catch (ValidationException e) {
            addFieldError("expirationDate", getText("error.end.date.before.start"));
            return false;
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * @return issuer resource name
     */
    protected abstract String getIssuerResource();

    /**
     * Delete the selected medical training credential.
     *
     * @return the struts forward.
     */
    protected String deleteMedicalTrainingCredential() {
        return deleteCredential(FormTypeEnum.CV);
    }

    /**
     * Method to support the AJAX call to retrieve a list of specialties based on a selected Certifying Board.
     *
     * @return the JSON List of Specialties.
     */
    public String getSpecialties() {
        specialtiesJson = Maps.newHashMap();
        if (getCertifyingBoard() != null) {
            specialtiesJson.put("specialties", getCertifyingBoard().getSpecialties());
        } else {
            specialtiesJson.put("specialties", emptyList);
        }
        return SUCCESS;
    }

    /**
     * Returns a list of all medical specialty certifying boards.
     *
     * @return medical specialty certifying boards
     */
    public abstract List<BOARD> getCertifyingBoards();

    /**
     * @return the specialtiesJson
     */
    public Map<String, List<SPECIALTY>> getSpecialtiesJson() {
        return specialtiesJson;
    }

    /**
     * @return the certifyingBoardId
     */
    public Long getCertifyingBoardId() {
        return (getCertifyingBoard() == null) ? null : getCertifyingBoard().getId();
    }

    /**
     * @param certifyingBoardId the certifyingBoardId to set
     */
    public void setCertifyingBoardId(Long certifyingBoardId) {
        setCertifyingBoard(getCertifyingBoardById(certifyingBoardId));
    }

    /**
     * Retrieves the certifying board with the given ID.
     *
     * @param certifyingBoardId certifying board ID
     * @return certifying board with the given ID
     */
    protected abstract BOARD getCertifyingBoardById(Long certifyingBoardId);

    /**
     * @return the typeId
     */
    public Long getSpecialtyId() {
        return (getSpecialty() == null) ? null : getSpecialty().getId();
    }

    /**
     * @return specialty
     */
    protected abstract SPECIALTY getSpecialty();

    /**
     * @param specialtyId the specialtyId to set
     */
    public void setSpecialtyId(Long specialtyId) {
        setSpecialty(getSpecialtyById(specialtyId));
    }

    /**
     * Retrieves the specialty with the given ID.
     *
     * @param specialtyId certifying board ID
     * @return specialty with the given ID
     */
    protected abstract SPECIALTY getSpecialtyById(Long specialtyId);

    /**
     * @param specialty specialty to set
     */
    protected abstract void setSpecialty(SPECIALTY specialty);

    @SuppressWarnings("ucd")
    // called from JSP pages
    public BOARD getCertifyingBoard() {
        return certifyingBoard;
    }

    @SuppressWarnings("ucd")
    // called from JSP pages
    public void setCertifyingBoard(BOARD certifyingBoard) {
        this.certifyingBoard = certifyingBoard;
    }

}
