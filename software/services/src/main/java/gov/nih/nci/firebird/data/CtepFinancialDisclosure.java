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
package gov.nih.nci.firebird.data;

import gov.nih.nci.firebird.common.ValidationResult;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.BooleanUtils.isTrue;

/**
 * CTEP's annual registration financial disclosure form.
 */
@Entity
@Table(name = "ctep_financial_disclosure")
public class CtepFinancialDisclosure extends AbstractAnnualRegistrationForm implements FinancialDisclosure {

    private static final long serialVersionUID = 1L;

    private Boolean monetaryGain;
    private Boolean financialInterest;
    private Boolean equityInSponsor;
    private Boolean otherSponsorPayments;

    private Set<FirebirdFile> supportingDocumentation = new HashSet<FirebirdFile>();
    private List<Organization> pharmaceuticalCompanies = new ArrayList<Organization>();


    /**
     * No-argument constructor for Hibernate / Struts.
     */
    public CtepFinancialDisclosure() {
        super();
    }

    CtepFinancialDisclosure(AnnualRegistration registration, FormType formType) {
        super(registration, formType);
    }

    @Override
    @Transient
    public Set<Person> getPersons() {
        return Collections.singleton(getInvestigator());
    }

    @Override
    @OneToOne(mappedBy = "financialDisclosure", optional = false)
    public AnnualRegistration getRegistration() {
        return super.getRegistration();
    }

    /**
     * Financial disclosure - List of pharmaceutical companies.
     * Field 5 in the PDF Form
     *
     * @return Organizations
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ctep_financial_disclosure_pharmaceuticals", joinColumns = @JoinColumn(name = "disclosure"),
            inverseJoinColumns = @JoinColumn(name = "pharmaceutical"))
    @ForeignKey(name = "ctep_financial_disclosure_fkey", inverseName = "pharmaceutical_fkey")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.MERGE,
        org.hibernate.annotations.CascadeType.REFRESH })
    @Sort(type = SortType.NATURAL)
    public List<Organization> getPharmaceuticalCompanies() {
        return pharmaceuticalCompanies;
    }

    /**
     *
     * @param pharmaceuticalCompanies the organizations that have financial arrangements
     */
    public void setPharmaceuticalCompanies(List<Organization> pharmaceuticalCompanies) {
        this.pharmaceuticalCompanies = pharmaceuticalCompanies;
    }

    /**
     * Question 1.
     *
     * @return the monetaryGain
     */
    @Column(name = "monetary_gain")
    public Boolean getMonetaryGain() {
        return monetaryGain;
    }

    /**
     * @param monetaryGain the monetaryGain to set
     */
    public void setMonetaryGain(Boolean monetaryGain) {
        this.monetaryGain = monetaryGain;
    }


    /**
     * Question 2.
     *
     * @return the financialInterest
     */
    @Column(name = "financial_interest")
    public Boolean getFinancialInterest() {
        return financialInterest;
    }

    /**
     * @param financialInterest the financialInterest to set
     */
    public void setFinancialInterest(Boolean financialInterest) {
        this.financialInterest = financialInterest;
    }

    /**
     * Question 3.
     *
     * @return the equityInSponsor
     */
    @Column(name = "equity_in_sponsor")
    public Boolean getEquityInSponsor() {
        return equityInSponsor;
    }

    /**
     * @param equityInSponsor the equityInSponsor to set
     */
    public void setEquityInSponsor(Boolean equityInSponsor) {
        this.equityInSponsor = equityInSponsor;
    }


    /**
     * Question 4.
     *
     * @return the otherSponsorPayments
     */
    @Column(name = "other_sponsor_payments")
    public Boolean getOtherSponsorPayments() {
        return otherSponsorPayments;
    }

    /**
     * @param otherSponsorPayments the otherSponsorPayments to set
     */
    public void setOtherSponsorPayments(Boolean otherSponsorPayments) {
        this.otherSponsorPayments = otherSponsorPayments;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ctep_financial_disclosure_documentation", joinColumns = @JoinColumn(name = "disclosure"),
            inverseJoinColumns = @JoinColumn(name = "firebirdfile"))
    @ForeignKey(name = "ctep_financial_disclosure_fkey", inverseName = "firebirdfile_fkey")
    @Cascade({ org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Override
    public Set<FirebirdFile> getSupportingDocumentation() {
        return supportingDocumentation;
    }

    /**
     *
     * @param supportingDocumentation supporting documents
     */
    public void setSupportingDocumentation(Set<FirebirdFile> supportingDocumentation) {
        this.supportingDocumentation = supportingDocumentation;
    }

    @Override
    public void validate(ValidationResult result, ResourceBundle resources) {
        super.validate(result, resources);
        checkQuestions(result, resources);
    }

    private void checkQuestions(ValidationResult result, ResourceBundle resources) {
        if (anyNull()
                || (getPharmaceuticalCompanies().isEmpty() && hasFinancialInterest())) {
            result.addFailure(createValidationFailure(resources,
                    "validation.failure.ctep.financial.disclosure.questions.incomplete"));
        }
    }

    private boolean anyNull() {
        return getEquityInSponsor() == null || getFinancialInterest() == null || getMonetaryGain() == null
                || getOtherSponsorPayments() == null;
    }

    private boolean hasFinancialInterest() {
        return isTrue(getEquityInSponsor()) || isTrue(getFinancialInterest()) || isTrue(getMonetaryGain())
                || isTrue(getOtherSponsorPayments());
    }

    @Override
    @Transient
    public boolean isAdditionalDocumentsUploaded() {
        return !getSupportingDocumentation().isEmpty();
    }

    @Override
    @Transient
    public int getNumberOfAdditionalDocuments() {
        return getSupportingDocumentation().size();
    }

    @Override
    @Transient
    public void copyForm(AbstractRegistrationForm form) {
        checkArgument(form instanceof CtepFinancialDisclosure,
                "Passed in form to copy wasn't an instance of CtepFinancialDisclosure");
        CtepFinancialDisclosure formToCopy = (CtepFinancialDisclosure) form;
        setEquityInSponsor(formToCopy.getEquityInSponsor());
        setFinancialInterest(formToCopy.getFinancialInterest());
        setMonetaryGain(formToCopy.getMonetaryGain());
        setOtherSponsorPayments(formToCopy.getOtherSponsorPayments());
        setPharmaceuticalCompanies(Lists.newArrayList(formToCopy.getPharmaceuticalCompanies()));
        setSupportingDocumentation(Sets.newHashSet(formToCopy.getSupportingDocumentation()));
    }

}
