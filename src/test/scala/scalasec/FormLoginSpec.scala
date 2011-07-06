package scalasec

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Assertions._
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices
import org.springframework.security.core.userdetails.{User, UserDetailsService}
import org.springframework.security.web.authentication.{NullRememberMeServices, LoginUrlAuthenticationEntryPoint}

/**
 * @author Luke Taylor
 */
class FormLoginSpec extends FlatSpec with ShouldMatchers with TestConversions {
  val filterChainWithForm = new FilterChain with FormLogin with LoginPageGenerator with AllowAllAuthentication
  val filterChainWithFormRememberMe = new FilterChain with FormLogin with Logout with RememberMe with LoginPageGenerator with AllowAllAuthentication {
    override val userDetailsService = new UserDetailsService {
      def loadUserByUsername(username: String) = new User(username, username, "ROLE_USER")
    }
  }
  val filterChainWithBasicForm = new FilterChain with BasicAuthentication with FormLogin with LoginPageGenerator with AllowAllAuthentication
  val filterChainWithFormBasic = new FilterChain with FormLogin with BasicAuthentication with AllowAllAuthentication


  "A FilterChain with FormLogin" should "have a LoginUrlAuthenticationEntryPoint" in {
    assert(filterChainWithForm.entryPoint.isInstanceOf[LoginUrlAuthenticationEntryPoint])
  }
  it should "have 9 filters" in {
    filterChainWithForm.filters.length should be (9)
  }
  it should "have a NullRememberMeServices" in {
    assert(filterChainWithForm.formLoginFilter.getRememberMeServices.isInstanceOf[NullRememberMeServices])
  }
  it should "allow setting a custom loginPage" in {
    val chain = new FilterChain with LoginPageGenerator with AllowAllAuthentication with FormLogin {
      override val loginPage = "/customLogin"
    }
    chain.entryPoint.asInstanceOf[LoginUrlAuthenticationEntryPoint].getLoginFormUrl should be ("/customLogin")
  }

  "A FilterChain with BasicAuthentication with FormLogin " should "have a LoginUrlAuthenticationEntryPoint" in {
    assert(filterChainWithBasicForm.entryPoint.isInstanceOf[LoginUrlAuthenticationEntryPoint])
  }

  "A FilterChain with FormLogin with BasicAuthentication" should "have a BasicAuthenticationEntryPoint" in {
    filterChainWithFormBasic.entryPoint should be theSameInstanceAs (filterChainWithFormBasic.basicAuthenticationEntryPoint)
  }
  it should "have 9 filters" in {
    filterChainWithFormBasic.filters.length should be (9)
  }

  "A FilterChain with FormLogin with RememberMe" should "have a TokenBasedRememberMeServices" in {
    assert(filterChainWithFormRememberMe.formLoginFilter.getRememberMeServices.isInstanceOf[TokenBasedRememberMeServices])
  }
}
