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
package gov.nih.nci.firebird.service.periodic;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import gov.nih.nci.firebird.service.annual.registration.AnnualRegistrationService;
import gov.nih.nci.firebird.service.ctep.esys.EsysIntegrationService;
import gov.nih.nci.firebird.service.sponsor.SponsorService;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang3.time.DateUtils;

import com.fiveamsolutions.nci.commons.util.HibernateHelper;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Provides timer creation and daily job execution functionality.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DailyJobServiceBean implements DailyJobService {

    static final String DAILY_JOB_SERVICE_BEAN_TIMER_MARKER = DailyJobServiceBean.class.getName();

    private TimerService timerService;
    private Date initialExpiration;
    private AnnualRegistrationService annualRegistrationService;
    private HibernateHelper hibernateHelper;
    private EsysIntegrationService esysIntegrationService;
    private SponsorService sponsorService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void initialize() {
        clearOldTimer();
        createTimer();
    }

    private void clearOldTimer() {
        for (Object timerObject : timerService.getTimers()) {
            Timer timer = (Timer) timerObject;
            if (isDailyJobServiceTimer(timer)) {
                timer.cancel();
            }
        }
    }

    private boolean isDailyJobServiceTimer(Timer timer) {
        return DAILY_JOB_SERVICE_BEAN_TIMER_MARKER.equals(timer.getInfo());
    }

    private void createTimer() {
        timerService.createTimer(initialExpiration, DateUtils.MILLIS_PER_DAY, DAILY_JOB_SERVICE_BEAN_TIMER_MARKER);
    }

    @Timeout
    @Override
    public void executeJobs(Timer timer) {
        if (!isEmpty(sponsorService.getSponsorWithAnnualRegistrationsExternalId())) {
            hibernateHelper.openAndBindSession();
            try {
                annualRegistrationService.createPendingRenewals();
                annualRegistrationService.sendRenewalReminders();
                esysIntegrationService.updateInvestigatorStatuses();
            } finally {
                hibernateHelper.unbindAndCleanupSession();
            }
        }
    }

    @Override
    public void createTestTimer(Date expiration) {
        timerService.createTimer(expiration, DailyJobServiceBean.class.getName());
    }

    @Resource
    void setTimerService(TimerService timerService) {
        this.timerService = timerService;
    }

    @Inject
    void setInitialExpiration(@Named("daily.job.runtime") String startTimeExpression) throws ParseException {
        Calendar startTime = DateUtils.toCalendar(DateUtils.parseDate(startTimeExpression, "H:mm"));
        initialExpiration = new Date();
        initialExpiration = DateUtils.setHours(initialExpiration, startTime.get(Calendar.HOUR_OF_DAY));
        initialExpiration = DateUtils.setMinutes(initialExpiration, startTime.get(Calendar.MINUTE));
        initialExpiration = DateUtils.setSeconds(initialExpiration, 0);
        initialExpiration = DateUtils.setMilliseconds(initialExpiration, 0);
        if (initialExpiration.before(new Date())) {
            initialExpiration = DateUtils.addDays(initialExpiration, 1);
        }
    }

    @Resource(mappedName = "firebird/AnnualRegistrationServiceBean/local")
    void setAnnualRegistrationService(AnnualRegistrationService annualRegistrationService) {
        this.annualRegistrationService = annualRegistrationService;
    }

    @Inject
    void setHibernateHelper(HibernateHelper hibernateHelper) {
        this.hibernateHelper = hibernateHelper;
    }

    @Resource(mappedName = "firebird/EsysIntegrationServiceBean/local")
    void setEsysIntegrationService(EsysIntegrationService esysIntegrationService) {
        this.esysIntegrationService = esysIntegrationService;
    }

    @Resource(mappedName = "firebird/SponsorServiceBean/local")
    void setSponsorService(SponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }

}
