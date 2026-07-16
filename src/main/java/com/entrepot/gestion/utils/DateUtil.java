package com.entrepot.gestion.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public final class DateUtil {

    private DateUtil() {
    }

    /**
     * Calcule la durée en mois complets entre deux dates.
     * 
     * @param debut date de début (incluse)
     * @param fin   date de fin (incluse)
     * @return nombre de mois complets
     */
    public static int calculerMois(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || fin.isBefore(debut)) {
            return 0;
        }
        Period period = Period.between(debut, fin);
        return period.getYears() * 12 + period.getMonths();
    }

    /**
     * Calcule la durée en jours restants après déduction des mois complets.
     * 
     * @param debut date de début (incluse)
     * @param fin   date de fin (incluse)
     * @return nombre de jours restants
     */
    public static int calculerJoursRestants(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || fin.isBefore(debut)) {
            return 0;
        }
        Period period = Period.between(debut, fin);
        return period.getDays();
    }

    /**
     * Calcule la durée totale en jours entre deux dates.
     * 
     * @param debut date de début
     * @param fin   date de fin
     * @return nombre de jours
     */
    public static long calculerJoursTotal(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || fin.isBefore(debut)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(debut, fin);
    }

    /**
     * Ajoute un nombre de mois à une date.
     */
    public static LocalDate ajouterMois(LocalDate date, int mois) {
        return date.plusMonths(mois);
    }

    /**
     * Calcule le nombre de mois complets écoulés entre dateDebut et dateFin,
     * en considérant que le mois commence au jour du mois de dateDebut.
     * Exemple : debut=20-07, fin=29-09 => mois complets = 2 (20-07→20-08, 20-08→20-09)
     */
    public static int calculerMoisCompletsDepuisDebut(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || fin.isBefore(debut)) {
            return 0;
        }
        int mois = 0;
        LocalDate courant = debut;
        while (!courant.plusMonths(1).isAfter(fin)) {
            courant = courant.plusMonths(1);
            mois++;
        }
        return mois;
    }

    /**
     * Retourne la date anniversaire du mois la plus proche avant ou égale à la date
     * donnée, basée sur le jour du mois de la date de début.
     * Exemple : debut=20-07, fin=29-09 => retourne 20-09
     */
    public static LocalDate trouverDerniereAnniversaire(LocalDate debut, LocalDate date) {
        if (debut == null || date == null || date.isBefore(debut)) {
            return debut;
        }
        int moisComplets = calculerMoisCompletsDepuisDebut(debut, date);
        return debut.plusMonths(moisComplets);
    }

    /**
     * Calcule la durée entre deux dates, retourne un objet Durree avec mois et jours.
     */
    public static Durree calculerDuree(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || fin.isBefore(debut)) {
            return new Durree(0, 0);
        }
        Period period = Period.between(debut, fin);
        int mois = period.getYears() * 12 + period.getMonths();
        int jours = period.getDays();
        return new Durree(mois, jours);
    }

    /**
     * Classe représentant une durée en mois et jours.
     */
    public static class Durree {
        private final int mois;
        private final int jours;

        public Durree(int mois, int jours) {
            this.mois = mois;
            this.jours = jours;
        }

        public int getMois() {
            return mois;
        }

        public int getJours() {
            return jours;
        }

        public boolean estNulle() {
            return mois == 0 && jours == 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (mois > 0) {
                sb.append(mois).append(" mois");
            }
            if (jours > 0) {
                if (sb.length() > 0) sb.append(" et ");
                sb.append(jours).append(" jour").append(jours > 1 ? "s" : "");
            }
            return sb.length() > 0 ? sb.toString() : "0 jour";
        }
    }
}
