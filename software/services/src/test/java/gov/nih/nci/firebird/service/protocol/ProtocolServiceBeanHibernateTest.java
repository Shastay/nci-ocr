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
package gov.nih.nci.firebird.service.protocol;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import gov.nih.nci.firebird.data.AbstractProtocolRegistration;
import gov.nih.nci.firebird.data.FormOptionality;
import gov.nih.nci.firebird.data.FormType;
import gov.nih.nci.firebird.data.FormTypeEnum;
import gov.nih.nci.firebird.data.InvestigatorProfile;
import gov.nih.nci.firebird.data.Organization;
import gov.nih.nci.firebird.data.Person;
import gov.nih.nci.firebird.data.Protocol;
import gov.nih.nci.firebird.data.ProtocolAgent;
import gov.nih.nci.firebird.data.ProtocolForm1572;
import gov.nih.nci.firebird.data.ProtocolPhase;
import gov.nih.nci.firebird.data.ProtocolRevision;
import gov.nih.nci.firebird.data.RegistrationStatus;
import gov.nih.nci.firebird.data.user.FirebirdUser;
import gov.nih.nci.firebird.data.user.SponsorRole;
import gov.nih.nci.firebird.exception.ValidationException;
import gov.nih.nci.firebird.service.registration.ProtocolRegistrationService;
import gov.nih.nci.firebird.service.sponsor.SponsorNotificationService;
import gov.nih.nci.firebird.test.AbstractHibernateTestCase;
import gov.nih.nci.firebird.test.FirebirdFileFactory;
import gov.nih.nci.firebird.test.FirebirdUserFactory;
import gov.nih.nci.firebird.test.FormTypeFactory;
import gov.nih.nci.firebird.test.InvestigatorProfileFactory;
import gov.nih.nci.firebird.test.OrganizationFactory;
import gov.nih.nci.firebird.test.PersonFactory;
import gov.nih.nci.firebird.test.ProtocolFactory;
import gov.nih.nci.firebird.test.RegistrationFactory;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ejb.SessionContext;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ProtocolServiceBeanHibernateTest extends AbstractHibernateTestCase {

    @Inject
    private ProtocolServiceBean bean;
    @Inject
    private FormTypeService formTypeService;
    @Inject
    private ProtocolRegistrationService registrationService;
    @Inject
    private ProtocolValidationService protocolValidationService;
    @Inject
    private ProtocolAgentService protocolAgentService;

    private Organization sponsor = OrganizationFactory.getInstance().create();
    private Organization leadOrganization = OrganizationFactory.getInstance().create();
    private Person principalInvestigator = PersonFactory.getInstance().create();
    private FormType form1572Type = FormTypeFactory.getInstance().create(FormTypeEnum.FORM_1572);
    private FormType financialDisclosureType = FormTypeFactory.getInstance().create(
            FormTypeEnum.FINANCIAL_DISCLOSURE_FORM);
    private FormType additionalAttachmentsType = FormTypeFactory.getInstance().create(
            FormTypeEnum.ADDITIONAL_ATTACHMENTS);
    private static File dataFile;

    @BeforeClass
    public static void setupFile() throws IOException {
        byte[] data = "123".getBytes();
        dataFile = File.createTempFile("test", ".blob");
        FileUtils.writeByteArrayToFile(dataFile, data);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        bean.setFormTypeService(formTypeService);
        bean.setRegistrationService(registrationService);
        bean.setProtocolValidationService(protocolValidationService);
        bean.setProtocolAgentService(protocolAgentService);
    }


    @Test
    public void testValidateAndSave() throws ValidationException {
        Protocol protocol = ProtocolFactory.getInstance().createWithForms();
        protocol.getAgents().clear();
        ProtocolAgent existingAgent = new ProtocolAgent("existing");
        protocol.getAgents().add(existingAgent);
        ProtocolAgent removedAgent = new ProtocolAgent("removed");
        protocol.getAgents().add(removedAgent);
        bean.validateAndSave(protocol);
        Protocol retrievedProtocol = bean.getById(protocol.getId());
        ProtocolFactory.getInstance().compare(protocol, retrievedProtocol);

        ProtocolAgent previouslyExistingAgent = new ProtocolAgent("previously existing");
        save(previouslyExistingAgent);

        Set<String> agentNames = Sets.newHashSet("existing", "new", "previously existing");
        protocol.updateAgents(agentNames);
        ProtocolFactory.getInstance().populateValues(protocol);
        saveAll(protocol.getRegistrationConfiguration().getAssociatedFormTypes());
        bean.validateAndSave(protocol);
        Iterator<ProtocolAgent> iterator = protocol.getAgents().iterator();
        assertEquals(existingAgent.getId(), iterator.next().getId());
        assertNotNull(iterator.next().getId());
        assertEquals(previouslyExistingAgent.getId(), iterator.next().getId());
        retrievedProtocol = bean.getById(protocol.getId());
        ProtocolFactory.getInstance().compare(protocol, retrievedProtocol);
    }

    @Test(expected = ValidationException.class)
    public void testSave_DuplicateProtocolNumber() throws ValidationException {
        SessionContext mockContext = mock(SessionContext.class);
        bean.setSessionContext(mockContext);
        Protocol protocol1 = ProtocolFactory.getInstance().create();
        saveAndFlush(protocol1);

        Protocol protocol2 = ProtocolFactory.getInstance().create();
        protocol2.setProtocolNumber(protocol1.getProtocolNumber());
        protocol2.setSponsor(protocol1.getSponsor());
        bean.validateAndSave(protocol2);
    }

    @Test(expected = ValidationException.class)
    public void testUpdate_ToDuplicateProtocolNumber() throws ValidationException {
        SessionContext mockContext = mock(SessionContext.class);
        bean.setSessionContext(mockContext);
        Protocol protocol1 = ProtocolFactory.getInstance().create();
        Protocol protocol2 = ProtocolFactory.getInstance().create();
        saveAll(protocol2.getRegistrationConfiguration().getAssociatedFormTypes());
        protocol2.setSponsor(protocol1.getSponsor());
        saveAndFlush(protocol1, protocol2);

        protocol2.setProtocolNumber(protocol1.getProtocolNumber());
        bean.validateAndSave(protocol2);
    }

    @Test
    public void testCreate() {
        saveAndFlush(form1572Type, financialDisclosureType, additionalAttachmentsType);
        Protocol protocol = bean.create();
        assertNotNull(protocol);
        assertEquals(FormOptionality.REQUIRED,
                protocol.getRegistrationConfiguration().getInvestigatorOptionality(form1572Type));
        assertEquals(FormOptionality.REQUIRED,
                protocol.getRegistrationConfiguration().getInvestigatorOptionality(financialDisclosureType));
        assertEquals(FormOptionality.SUPPLEMENTARY,
                protocol.getRegistrationConfiguration().getInvestigatorOptionality(additionalAttachmentsType));
        assertEquals(FormOptionality.REQUIRED,
                protocol.getRegistrationConfiguration().getSubinvestigatorOptionality(form1572Type));
        assertEquals(FormOptionality.REQUIRED,
                protocol.getRegistrationConfiguration().getSubinvestigatorOptionality(financialDisclosureType));
        assertEquals(FormOptionality.SUPPLEMENTARY, protocol.getRegistrationConfiguration()
                .getSubinvestigatorOptionality(additionalAttachmentsType));
    }

    @Test
    public void testGetAll() {
        saveAndFlush(form1572Type, financialDisclosureType);
        Protocol protocol = bean.create();
        ProtocolFactory.getInstance().populateValues(protocol);
        save(protocol);
        assertEquals(1, bean.getAll().size());
        assertEquals(protocol, bean.getAll().get(0));
    }

    @Test
    public void testGetProtocolsBySponsor() {
        Organization organization1 = OrganizationFactory.getInstance().create();
        Organization organization2 = OrganizationFactory.getInstance().create();
        Organization organization3 = OrganizationFactory.getInstance().create();
        Protocol protocol1 = ProtocolFactory.getInstance().create(organization1);
        Protocol protocol2 = ProtocolFactory.getInstance().create(organization2);
        Protocol protocol3 = ProtocolFactory.getInstance().create(organization3);
        FirebirdUser user = FirebirdUserFactory.getInstance().create();
        SponsorRole role1 = user.addSponsorRepresentativeRole(organization1);

        save(organization1, organization2, organization3, protocol1, protocol2, protocol3, user);
        List<Protocol> protocols = bean.getProtocols(user, Sets.newHashSet(role1.getVerifiedSponsorGroupName()));
        assertEquals(1, protocols.size());
        assertSamePersistentObjects(protocol1, protocols.get(0));
    }

    @Test
    public void testUpdateProtocol() throws ValidationException {
        bean.setSponsorNotificationService(mock(SponsorNotificationService.class));
        InvestigatorProfile profile1 = InvestigatorProfileFactory.getInstance().create();
        InvestigatorProfile profile2 = InvestigatorProfileFactory.getInstance().create();
        save(sponsor, form1572Type, financialDisclosureType, profile1, profile2);

        Protocol protocol = makeProtocol();
        AbstractProtocolRegistration registrationSubmitted = RegistrationFactory.getInstance()
                .createInvestigatorRegistration(profile1, protocol);
        registrationSubmitted.setStatus(RegistrationStatus.SUBMITTED);
        protocol.addRegistration(registrationSubmitted);
        AbstractProtocolRegistration registrationNotStarted = RegistrationFactory.getInstance()
                .createInvestigatorRegistration(profile2, protocol);
        registrationNotStarted.setStatus(RegistrationStatus.NOT_STARTED);
        protocol.addRegistration(registrationNotStarted);
        save(protocol);
        Protocol snapshot = protocol.createCopy();

        protocol.getRegistrationConfiguration().setInvestigatorOptionality(form1572Type, FormOptionality.OPTIONAL);
        protocol.getRegistrationConfiguration().setInvestigatorOptionality(financialDisclosureType,
                FormOptionality.REQUIRED);
        protocol.getRegistrationConfiguration().setSubinvestigatorOptionality(form1572Type, FormOptionality.REQUIRED);
        protocol.setProtocolTitle("some new title");

        bean.updateProtocol(snapshot, protocol, "change is good");
        assertEquals(1, protocol.getRevisionHistory().size());
        ProtocolRevision change = protocol.getRevisionHistory().iterator().next();
        Set<String> expected = Sets.newHashSet(
                getPropertyText("protocol.change.title.investigator.message", snapshot.getProtocolTitle(),
                        protocol.getProtocolTitle()),
                getPropertyText("protocol.change.form.optionality.investigator.investigator.message",
                        FormTypeEnum.FORM_1572.getDisplay(), FormOptionality.REQUIRED.getDisplay(),
                        FormOptionality.OPTIONAL.getDisplay()),
                getPropertyText("protocol.change.form.optionality.investigator.investigator.message",
                        FormTypeEnum.FINANCIAL_DISCLOSURE_FORM.getDisplay(), FormOptionality.NONE.getDisplay(),
                        FormOptionality.REQUIRED.getDisplay()),
                getPropertyText("protocol.change.form.optionality.subinvestigator.investigator.message",
                        FormTypeEnum.FORM_1572.getDisplay(), FormOptionality.OPTIONAL.getDisplay(),
                        FormOptionality.REQUIRED.getDisplay()));
        for (String msg : change.getInvestigatorModificationDescriptions()) {
            assertTrue("expected " + msg, expected.remove(msg));
        }
        assertTrue(expected.toString(), expected.isEmpty());
        assertEquals(RegistrationStatus.PROTOCOL_UPDATED, registrationSubmitted.getStatus());
        assertEquals(RegistrationStatus.NOT_STARTED, registrationNotStarted.getStatus());
    }

    @Test
    public void testUpdateProtocol_NewForm() throws ValidationException {
        InvestigatorProfile profile = InvestigatorProfileFactory.getInstance().create();
        save(sponsor, form1572Type, financialDisclosureType, profile);

        Protocol protocol = makeProtocol();
        protocol.getRegistrationConfiguration().setInvestigatorOptionality(form1572Type, FormOptionality.NONE);
        AbstractProtocolRegistration registrationNotStarted = RegistrationFactory.getInstance()
                .createInvestigatorRegistration(profile, protocol);
        registrationNotStarted.setStatus(RegistrationStatus.NOT_STARTED);
        protocol.addRegistration(registrationNotStarted);
        save(protocol);
        Protocol snapshot = protocol.createCopy();
        assertNull(registrationNotStarted.getForm1572());

        protocol.getRegistrationConfiguration().setInvestigatorOptionality(form1572Type, FormOptionality.REQUIRED);
        protocol.getRegistrationConfiguration().setSubinvestigatorOptionality(form1572Type, FormOptionality.REQUIRED);

        bean.updateProtocol(snapshot, protocol, "change is good");
        assertNotNull(registrationNotStarted.getForm1572());
    }

    @Test
    public void testUpdateProtocol_ExistingForm() throws ValidationException {
        InvestigatorProfile profile = InvestigatorProfileFactory.getInstance().create();
        save(sponsor, form1572Type, financialDisclosureType, profile);

        Protocol protocol = makeProtocol();
        AbstractProtocolRegistration registrationNotStarted = RegistrationFactory.getInstance()
                .createInvestigatorRegistration(profile, protocol);
        registrationNotStarted.setStatus(RegistrationStatus.NOT_STARTED);
        protocol.addRegistration(registrationNotStarted);
        save(protocol);
        Protocol snapshot = protocol.createCopy();
        ProtocolForm1572 savedProtocolForm = registrationNotStarted.getForm1572();
        assertNotNull(savedProtocolForm);

        protocol.getRegistrationConfiguration().setInvestigatorOptionality(form1572Type, FormOptionality.REQUIRED);
        protocol.getRegistrationConfiguration().setSubinvestigatorOptionality(form1572Type, FormOptionality.REQUIRED);

        bean.updateProtocol(snapshot, protocol, "change is good");
        assertSame(savedProtocolForm, registrationNotStarted.getForm1572());
    }

    @Test
    public void testUpdateProtocolUnchanged() throws ValidationException {
        save(sponsor, form1572Type, financialDisclosureType);
        Protocol protocol = makeProtocol();
        save(protocol);
        Protocol snapshot = protocol.createCopy();
        bean.updateProtocol(snapshot, protocol, "i will never change!");
        assertTrue(protocol.getRevisionHistory().isEmpty());
    }

    @Test(expected = ValidationException.class)
    public void testUpdateProtocolNoComment() throws ValidationException {
        save(sponsor, form1572Type, financialDisclosureType);
        Protocol protocol = makeProtocol();
        save(protocol);
        Protocol snapshot = protocol.createCopy();
        protocol.setProtocolNumber("new number");
        bean.updateProtocol(snapshot, protocol, null);
    }

    private Protocol makeProtocol() {
        Protocol protocol = new Protocol();
        protocol.setSponsor(sponsor);
        protocol.addLeadOrganization(leadOrganization, principalInvestigator);
        protocol.setPhase(ProtocolPhase.PHASE_0);
        protocol.setProtocolNumber("PPPPPNNNNN");
        protocol.setProtocolTitle("PPPPP TTTTT");
        protocol.getDocuments().add(FirebirdFileFactory.getInstance().create());

        protocol.getRegistrationConfiguration().setInvestigatorOptionality(form1572Type, FormOptionality.REQUIRED);
        protocol.getRegistrationConfiguration().setSubinvestigatorOptionality(form1572Type, FormOptionality.OPTIONAL);
        return protocol;
    }

}
