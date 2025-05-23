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
package gov.nih.nci.firebird.nes.organization;

import gov.nih.nci.coppa.po.CorrelationNode;
import gov.nih.nci.coppa.po.IdentifiedOrganization;
import gov.nih.nci.coppa.po.ResearchOrganization;
import gov.nih.nci.coppa.po.StringMap;
import gov.nih.nci.coppa.services.business.business.common.BusinessI;
import gov.nih.nci.coppa.services.entities.organization.common.OrganizationI;
import gov.nih.nci.coppa.services.structuralroles.researchorganization.common.ResearchOrganizationI;
import gov.nih.nci.firebird.data.Organization;
import gov.nih.nci.firebird.nes.AbstractNesData;
import gov.nih.nci.firebird.nes.NesId;
import gov.nih.nci.firebird.nes.common.ValidationErrorTranslator;
import gov.nih.nci.iso21090.extensions.Id;

import java.rmi.RemoteException;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Provides methods for integration with the NES ResearchOrganization service.
 */
@SuppressWarnings("PMD.TooManyMethods")
// Methods broken down for clarity
public class ResearchOrganizationIntegrationServiceBean extends AbstractCorrelationIntegrationServiceBean implements
        ResearchOrganizationIntegrationService {

    private final ResearchOrganizationI researchOrganizationService;
    private final ResearchOrganizationTranslator translator;

    @Inject
    @SuppressWarnings("PMD.ExcessiveParameterList")
    // class requires many dependencies via Guice
    ResearchOrganizationIntegrationServiceBean(BusinessI businessService, OrganizationI organizationService,
            IdentifiedOrganizationIntegrationService identifiedOrganizationService,
            ValidationErrorTranslator errorTranslator, ResearchOrganizationI researchOrganizationService,
            ResearchOrganizationTranslator translator) {
        super(businessService, organizationService, identifiedOrganizationService, errorTranslator);
        this.researchOrganizationService = researchOrganizationService;
        this.translator = translator;
    }

    @Override
    public void create(Organization organization, final ResearchOrganizationType type) {
        OrganizationCreator creator = new OrganizationCreator() {
            @Override
            public Id create(Organization organization) throws RemoteException {
                getExternalData(organization).setResearchOrganizationType(type);
                return callCreate(organization, type);
            }
        };
        create(organization, creator);
    }

    private Id callCreate(Organization organization, ResearchOrganizationType type) throws RemoteException {
        createPlayer(organization, translator);
        return researchOrganizationService.create(translator.toResearchOrganization(organization, type));
    }

    @Override
    Organization toFirebirdOrganization(CorrelationNode correlationNode) {
        gov.nih.nci.coppa.po.Organization player = (gov.nih.nci.coppa.po.Organization) correlationNode.getPlayer()
                .getContent().get(0);
        ResearchOrganization researchOrganization = (ResearchOrganization) correlationNode.getCorrelation()
                .getContent().get(0);
        return translator.toFirebirdOrganization(researchOrganization, player);
    }

    @Override
    StringMap getValidationResults(Organization organization) throws RemoteException {
        StringMap results = researchOrganizationService.validate(translator.toResearchOrganization(organization, null));
        StringMap playerResults = getOrganizationService().validate(translator.toNesOrganization(organization));
        return combineValidationResults(results, playerResults);
    }

    @Override
    public List<Organization> searchByName(String searchName, ResearchOrganizationType type) {
        OrganizationSearcher searcher = createNameSearch(searchName, type);
        return performSearch(searcher);
    }

    private OrganizationSearcher createNameSearch(final String searchName, final ResearchOrganizationType type) {
        return new OrganizationSearcher() {
            public List<Organization> search() throws RemoteException {
                return getNameMatches(searchName, type);
            }
        };
    }

    private List<Organization> getNameMatches(String searchName, ResearchOrganizationType type) throws RemoteException {
        ResearchOrganization researchOrganization = new ResearchOrganization();
        researchOrganization.setName(translator.toNesName(searchName));
        researchOrganization.setTypeCode(translator.toCd(type));
        CorrelationNode searchNode = createCorrelationNode();
        searchNode.getCorrelation().getContent().add(researchOrganization);
        List<CorrelationNode> correlationNodes = searchCorrelations(searchNode);
        return toFirebirdOrganizations(correlationNodes);
    }

    @Override
    public List<Organization> searchByAssignedIdentifier(String assignedIdentifier, ResearchOrganizationType type) {
        OrganizationSearcher searcher = createIdentifiedOrganizationExtensionSearch(assignedIdentifier, type);
        return searchByAssignedIdentifier(searcher, assignedIdentifier);
    }

    private OrganizationSearcher createIdentifiedOrganizationExtensionSearch(final String extension,
            final ResearchOrganizationType type) {
        return new OrganizationSearcher() {
            public List<Organization> search() throws RemoteException {
                return getOrganizations(getIdentifiedOrganizationService().getIdentifiedOrganizations(extension), type);
            }
        };
    }

    private List<Organization> getOrganizations(List<IdentifiedOrganization> identifiedOrganizations,
            ResearchOrganizationType type) throws RemoteException {
        List<Id> playerIds = getPlayerIds(identifiedOrganizations);
        return getOrganizationsByPlayerIds(playerIds, type);
    }

    private List<Organization> getOrganizationsByPlayerIds(List<Id> playerIds, ResearchOrganizationType type)
            throws RemoteException {
        List<Organization> organizations = Lists.newArrayList();
        for (Id playerId : playerIds) {
            organizations.addAll(getByIdentifiedOrganizationPlayerId(new NesId(playerId), type));
        }
        return organizations;
    }

    List<Organization> getByIdentifiedOrganizationPlayerId(NesId playerNesId, ResearchOrganizationType type)
            throws RemoteException {
        List<Organization> organizations = Lists.newArrayList();
        ResearchOrganization[] researchOrganizations = researchOrganizationService
                .getByPlayerIds(new Id[] {playerNesId.toId()});
        if (researchOrganizations != null) {
            gov.nih.nci.coppa.po.Organization player = getOrganizationService().getById(playerNesId.toId());
            organizations.addAll(filterAndTranslate(type, researchOrganizations, player));
        }
        return organizations;
    }

    private List<Organization> filterAndTranslate(ResearchOrganizationType type,
            ResearchOrganization[] researchOrganizations, gov.nih.nci.coppa.po.Organization player) {
        List<Organization> organizations = Lists.newArrayList();
        for (ResearchOrganization researchOrganization : researchOrganizations) {
            if (researchOrganization.getTypeCode().getCode().equals(type.getCode())) {
                organizations.add(translator.toFirebirdOrganization(researchOrganization, player));
            }
        }
        return organizations;
    }
    
    @Override
    AbstractNesData createNesExternalData() {
        return new ResearchOrganizationData();
    }

    @Override
    ResearchOrganizationData getExternalData(Organization organization) {
        return (ResearchOrganizationData) super.getExternalData(organization);
    }
}
