package butterknife.internal;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static butterknife.internal.ProcessorTestUtilities.butterknifeProcessors;
import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class ResourceDrawableTest {
  @Test public void simple() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import android.graphics.drawable.Drawable;",
        "import butterknife.ResourceDrawable;",
        "public class Test extends Activity {",
        "  @ResourceDrawable(1) Drawable one;",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder",
        Joiner.on('\n').join(
            "package test;",
            "import android.content.res.Resources;",
            "import butterknife.ButterKnife.Finder;",
            "import butterknife.ButterKnife.ViewBinder;",
            "public class Test$$ViewBinder<T extends test.Test> implements ViewBinder<T> {",
            "  @Override public void bind(final Finder finder, final T target, Object source) {",
            "    Resources res = finder.getContext(source).getResources();",
            "    target.one = res.getDrawable(1);",
            "  }",
            "  @Override public void unbind(T target) {",
            "  }",
            "}"
        ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void typeMustBeDrawable() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.app.Activity;",
        "import butterknife.ResourceDrawable;",
        "public class Test extends Activity {",
        "  @ResourceDrawable(1) String one;",
        "}"
    ));

    ASSERT.about(javaSource()).that(source)
        .processedWith(butterknifeProcessors())
        .failsToCompile()
        .withErrorContaining("@ResourceDrawable field type must be 'Drawable'. (test.Test.one)")
        .in(source).onLine(5);
  }
}
