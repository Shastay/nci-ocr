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

package gov.nih.nci.firebird.proxy;

import java.rmi.RemoteException;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;

import com.google.inject.Provider;
import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;
import gov.nih.nci.coppa.common.faults.TooManyResultsFault;
import gov.nih.nci.coppa.po.faults.EntityValidationFault;
import gov.nih.nci.coppa.po.faults.NullifiedEntityFault;
import gov.nih.nci.firebird.proxy.PoolingHandler;
import gov.nih.nci.firebird.proxy.RetryHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.pool.ObjectPool;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * test the parts of the module.
 */
@SuppressWarnings("unchecked")
public class FaultTolerantProxyFactoryTest {

    @SuppressWarnings("rawtypes")
    private static final List VALID_EXCEPTIONS = Arrays.asList(TooManyResultsFault.class, NullifiedEntityFault.class, EntityValidationFault.class);

    private GlobusCredential mockProxy = mock(GlobusCredential.class);
    private Provider<TestClient> clientProvider = new Provider<TestClient>() {
            @Override public TestClient get() {
                try {
                    return new TestClient(mockProxy);
                } catch (MalformedURIException ex) {
                    throw new RuntimeException(ex);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };

    @Test
    public void testWrapper() throws Exception {
        when(mockProxy.getTimeLeft()).thenReturn(300L);
        ProxyConfiguration configuration = new ProxyConfiguration(2, 1, 3, VALID_EXCEPTIONS);
        TestClientI wrappedClient = FaultTolerantProxyFactory.create(clientProvider, TestClientI.class, configuration);
        verifyProxyChain(wrappedClient);
        verifyInvocation(wrappedClient);
    }

    @Test(expected = NoSuchElementException.class)
    public void testExpiredCredential() throws Exception {
        when(mockProxy.getTimeLeft()).thenReturn(0L);
        ProxyConfiguration configuration = new ProxyConfiguration(2, 1, 3, VALID_EXCEPTIONS);
        TestClientI wrappedClient = FaultTolerantProxyFactory.create(clientProvider, TestClientI.class, configuration);
        verifyInvocation(wrappedClient);
    }

    private void verifyProxyChain(TestClientI wrappedClient) throws Exception {
        assertTrue(Proxy.isProxyClass(wrappedClient.getClass()));
        RetryHandler retryHandler = (RetryHandler) Proxy.getInvocationHandler(wrappedClient);
        Provider<TestClientI> provider = (Provider<TestClientI>) retryHandler.getClientProvider();

        TestClientI pooledClient = provider.get();
        assertTrue(Proxy.isProxyClass(pooledClient.getClass()));
        PoolingHandler poolingHandler = (PoolingHandler) Proxy.getInvocationHandler(pooledClient);
        ObjectPool<Object> pool = poolingHandler.getPool();
        TestClientI client = (TestClientI) pool.borrowObject();
        assertNotNull(client);

        assertTrue(retryHandler.getValidExceptions().containsAll(VALID_EXCEPTIONS));
        assertTrue(poolingHandler.getValidExceptions().containsAll(VALID_EXCEPTIONS));
    }

    private void verifyInvocation(TestClientI wrappedClient) {
        wrappedClient.pass();
        try {
            wrappedClient.fail();
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    public static class TestClient extends ServiceSecurityClient implements TestClientI {

        public TestClient(GlobusCredential proxy) throws MalformedURIException, RemoteException {
            super("http://example.com", proxy);
        }

        @Override public void pass() { }
        @Override public void fail() { throw new IllegalStateException(); }
    }

    public interface TestClientI {
        void fail();
        void pass();
    }
}
