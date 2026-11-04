import { useEffect, useRef } from 'react';

import { useUpdateMe, type User } from '../../api/generated';
import { hasSavedLanguage, useLanguage } from '../../i18n';

/**
 * El idioma de la cuenta (User.locale) y el del panel van juntos: con él llegan los correos y los
 * avisos de Telegram.
 * - Al entrar en un dispositivo sin idioma elegido a mano, el panel adopta el de la cuenta.
 * - Si el panel está en otro idioma (se eligió aquí o se cambió en Ajustes), se guarda en la
 *   cuenta con PATCH /users/me, una vez por cambio. `auth.user` no se refresca tras guardar: se
 *   compara con el último idioma guardado, no con el de la sesión.
 */
export function useAccountLanguage(user: User | null) {
  const { language, setLanguage } = useLanguage();
  const { mutate } = useUpdateMe();
  const userId = useRef<string | null>(null);
  const accountLanguage = useRef<string | null>(null);

  useEffect(() => {
    if (!user) {
      userId.current = null;
      accountLanguage.current = null;
      return;
    }
    if (userId.current !== user.id) {
      userId.current = user.id;
      accountLanguage.current = user.locale;
      if (!hasSavedLanguage() && user.locale !== language) {
        setLanguage(user.locale);
        return;
      }
    }
    if (language !== accountLanguage.current) {
      accountLanguage.current = language;
      mutate({ data: { locale: language } });
    }
  }, [user, language, setLanguage, mutate]);
}
