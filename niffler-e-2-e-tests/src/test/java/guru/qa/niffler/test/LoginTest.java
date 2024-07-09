package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.entity.AuthorityEntity;
import guru.qa.niffler.data.entity.CurrencyValues;
import guru.qa.niffler.data.entity.UserAuthEntity;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.data.repository.UserRepositoryHibernate;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class LoginTest extends BaseWebTest {

    @BeforeEach
    void configure() {
        new WireMock("http://userdata.niffler.dc", 8089).register(get(urlPathEqualTo("/internal/users/current"))
                .willReturn(okJson(
                        """
                                {
                                    "id": "{{randomValue type='UUID'}}",
                                    "username": "{{request.query.username}}",
                                    "firstname": "Dmitrii",
                                    "surname": "",
                                    "currency": "KZT"
                                }
                                """
                ))
        );
    }

    @Test
    @TestUser
    void loginTest(@User(User.Point.OUTER) UserJson user) {
        Selenide.open(CFG.frontUrl());
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.username());
        $("input[name='password']").setValue(user.testData().password());
        $("button[type='submit']").click();
        $(".header__avatar").should(visible);
    }
}
