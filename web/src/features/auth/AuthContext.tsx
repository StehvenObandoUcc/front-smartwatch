import {
  createContext,
  use,
  useCallback,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import { useQueryClient } from '@tanstack/react-query';

import { getMe, logout as apiLogout, type AuthSession, type User } from '../../api/generated';
import { refreshSession, setAccessToken, setSessionLostHandler } from '../../lib/http';

type Auth = { status: 'loading' } | { status: 'anon' } | { status: 'authed'; user: User };

type Value = {
  auth: Auth;
  signIn: (session: AuthSession) => void;
  signOut: () => Promise<void>;
};

const AuthContext = createContext<Value | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [auth, setAuth] = useState<Auth>({ status: 'loading' });
  const queryClient = useQueryClient();

  const clear = useCallback(() => {
    setAccessToken(null);
    queryClient.clear();
    setAuth({ status: 'anon' });
  }, [queryClient]);

  useEffect(() => {
    setSessionLostHandler(clear);
    let active = true;
    void (async () => {
      const ok = await refreshSession();
      const user = ok ? await getMe().catch(() => null) : null;
      if (active) setAuth(user ? { status: 'authed', user } : { status: 'anon' });
    })();
    return () => {
      active = false;
    };
  }, [clear]);

  const value = useMemo<Value>(
    () => ({
      auth,
      signIn: (session) => {
        setAccessToken(session.tokens.accessToken);
        setAuth({ status: 'authed', user: session.user });
      },
      signOut: async () => {
        await apiLogout().catch(() => undefined);
        clear();
      },
    }),
    [auth, clear],
  );
  return <AuthContext value={value}>{children}</AuthContext>;
}

export function useAuth(): Value {
  const value = use(AuthContext);
  if (!value) throw new Error('useAuth fuera de AuthProvider');
  return value;
}
