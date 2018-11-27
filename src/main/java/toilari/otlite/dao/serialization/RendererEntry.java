package toilari.otlite.dao.serialization;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.dao.TextureDAO;
import toilari.otlite.view.renderer.IRenderer;

/**
 * Apuluokka piirtäjien luokkatietojen käsittelyyn.
 *
 * @param <R> piirtäjän tyyppi
 * @param <C> piirtäjän piirtokontekstin tyyppi
 */
class RendererEntry<R extends IRenderer, C> {
    @Getter private final RendererAdapter.RendererFactory<R, C> factory;
    @Getter private final Class<? extends C> contextClass;

    RendererEntry(@NonNull RendererAdapter.RendererFactory<R, C> factory, @NonNull Class<? extends C> contextClass) {
        this.factory = factory;
        this.contextClass = contextClass;
    }

    R createRenderer(TextureDAO textureDAO, Object context) {
        return this.factory.provide(textureDAO, (C) context);
    }
}
