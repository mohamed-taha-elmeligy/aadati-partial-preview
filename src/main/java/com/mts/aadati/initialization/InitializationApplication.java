
package com.mts.aadati.initialization;

import com.mts.aadati.autogenerators.DayAutoGenerator;
import com.mts.aadati.autogenerators.WeekAutoGenerator;
import com.mts.aadati.entities.*;
import com.mts.aadati.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Component
@RequiredArgsConstructor
public class InitializationApplication {

    private static final Logger log = LoggerFactory.getLogger(InitializationApplication.class);

    private final TaskPriorityLevelService priorityLevelService ;
    private final UserService userService ;
    private final HabitDayWeekService habitDayWeekService;
    private final HabitCategoryService habitCategoryService ;
    private final HabitWeekService habitWeekService;
    private final HabitCalendarService habitCalendarService ;
    private final RoleService roleService ;

    private final InitializationHabitCompletion initializationHabitCompletion ;
    private final InitializationTaskCompletion initializationTaskCompletion ;
    private final InitializationPercentageDay initializationPercentageDay ;
    private final InitializationPercentageWeek initializationPercentageWeek ;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;


    private final DayAutoGenerator dayAutoGenerator ;

    private final AtomicBoolean isFirstRun = new AtomicBoolean(true);

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    protected void onAppStart() {
        log.info("Application started - running initialization");
        initialization();
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    protected void onDailySchedule() {
        log.debug("Daily schedule triggered");
        initialization();
    }

    private synchronized void initialization() {
        if (isFirstRun.compareAndSet(true, false)) {
            log.info("First run - full initialization");
            addRolesToDB();
            addAllTaskPriorityLevelToDB();
            addCategoryToDB();
            addDayOfWeekToDB();
            addUserToDB();
            addWeekToDB();
            addDaysToDB();
            initializationHabitCompletion.addLaterHabitToCompletion();
            initializationTaskCompletion.addLaterTasksToCompletion();
            initializationPercentageDay.initializationMulti();
            initializationPercentageWeek.initializationMulti();
        } else {
            log.debug("Regular run - daily tasks");
            if (LocalDate.now().getDayOfYear() == 1) {
                log.info("New year detected - adding weeks");
                addWeekToDB();
            }
            addDaysToDB();
            initializationHabitCompletion.addHabitToCompletion();
            initializationTaskCompletion.addTasksToCompletion();
            initializationPercentageDay.initializationDay();
            initializationPercentageWeek.initializationWeek();
        }
    }


    public void addAllTaskPriorityLevelToDB(){
        log.debug(" addAllTaskPriorityLevelToDB called ");
        if(priorityLevelService.addAllTaskPriorityLevelEntity(List.of(
                TaskPriorityLevel.createUrgent(),
                TaskPriorityLevel.createHigh(),
                TaskPriorityLevel.createImportant(),
                TaskPriorityLevel.createMedium(),
                TaskPriorityLevel.createLow()
        )))  log.info(" Task Priority Level data added successfully");
        else log.error("Failed to add data (Task Priority Level)");
    }

    private void addUserToDB() {
        log.debug("addUserToDB called ");
        List<Role> managedRoles = roleService.findEntityIsDeleteFalse();

        User user = User.builder()
                .firstName("Mohamed")
                .lastName("Taha Elmeligy")
                .username(adminUsername)
                .password(adminPassword)
                .email("test@gmail.com")
                .emailVerified(true)
                .build();
        user.setRoles(managedRoles);

        if (userService.addUser(user).isPresent())
            log.debug("User added successfully with roles: {}", managedRoles);
        else log.error("Failed to add data (User)");
    }



    private void addDayOfWeekToDB(){
        log.debug("addDayOfWeekToDB called ");
        if (habitDayWeekService.addAll(List.of(
                new HabitDayWeek(DayOfWeek.of(1)),
                new HabitDayWeek(DayOfWeek.of(2)),
                new HabitDayWeek(DayOfWeek.of(3)),
                new HabitDayWeek(DayOfWeek.of(4)),
                new HabitDayWeek(DayOfWeek.of(5)),
                new HabitDayWeek(DayOfWeek.of(6)),
                new HabitDayWeek(DayOfWeek.of(7))
                ))) log.info("Habit Day of Week data added successfully");
        else log.error("Failed to add data (Habit Day of Week)");
    }

    private void addCategoryToDB(){
        log.debug("addCategoryToDB called ");
        List<HabitCategory> defaultCategories = List.of(
                HabitCategory.builder().name("Fitness").description("Activities to stay fit").color("#FF5733").build(),
                HabitCategory.builder().name("Health").description("Healthy habits & routines").color("#33FF57").build(),
                HabitCategory.builder().name("Learning").description("Study, courses, and skill improvement").color("#3357FF").build(),
                HabitCategory.builder().name("Productivity").description("Work, planning, and time management").color("#FFC300").build(),
                HabitCategory.builder().name("Mindfulness").description("Meditation, journaling, mental wellness").color("#DAF7A6").build(),
                HabitCategory.builder().name("Finance").description("Saving, budgeting, financial goals").color("#900C3F").build(),
                HabitCategory.builder().name("Social").description("Networking, family, friends").color("#581845").build(),
                HabitCategory.builder().name("Nutrition").description("Healthy eating and diet").color("#FF6F61").build(),
                HabitCategory.builder().name("Hydration").description("Drinking enough water").color("#1E90FF").build(),
                HabitCategory.builder().name("Sleep").description("Sleep routines and hygiene").color("#8A2BE2").build(),
                HabitCategory.builder().name("Hobbies").description("Creative and leisure activities").color("#FF69B4").build(),
                HabitCategory.builder().name("Self-Care").description("Personal care and relaxation").color("#00CED1").build(),
                HabitCategory.builder().name("Chores").description("Household tasks and cleaning").color("#FFD700").build(),
                HabitCategory.builder().name("Environment").description("Eco-friendly habits").color("#32CD32").build(),
                HabitCategory.builder().name("Reading").description("Books, articles, knowledge").color("#FF8C00").build(),
                HabitCategory.builder().name("Tech Detox").description("Limit screen time").color("#8B0000").build(),
                HabitCategory.builder().name("Mindset").description("Positive thinking & reflection").color("#20B2AA").build(),
                HabitCategory.builder().name("Spirituality").description("Spiritual growth and practices").color("#BA55D3").build(),
                HabitCategory.builder().name("Creativity").description("Art, music, writing").color("#FF4500").build(),
                HabitCategory.builder().name("Volunteering").description("Community service and helping others").color("#2E8B57").build()
        );
        if (habitCategoryService.addEntityAllHabitCategory(defaultCategories))
            log.info("Habit Category data added successfully");
        else log.error("Failed to add data (Habit Category)");
    }

    private void addWeekToDB() {
        log.debug("addWeekToDB called");

        Optional<HabitWeek> habitWeek = habitWeekService.findEntityFirstByOrderByEndWeekDesc();
        LocalDate startWeek;

        if (habitWeek.isEmpty()) {
            LocalDate firstDay = LocalDate.of(LocalDate.now().getYear(), 1, 1);
            startWeek = firstDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
            log.info("No weeks found, starting from first Monday of the year: {}", startWeek);
        } else {
            LocalDate nextStart = habitWeek.get().getEndWeek().plusDays(1);

            if (nextStart.getYear() != LocalDate.now().getYear()) {
                log.info("All weeks for this year ({}) already exist. Last week ended at: {}",
                        habitWeek.get().getYear(), habitWeek.get().getEndWeek());
                return;
            }
            startWeek = nextStart;
            log.info("Next week will start at: {}", startWeek);
        }

        List<HabitWeek> generatedWeeks = WeekAutoGenerator.weekAutoGenerator(startWeek);
        List<HabitWeek> savedWeeks = habitWeekService.saveEntityAll(generatedWeeks);

        if (!savedWeeks.isEmpty()) {
            log.info("{} HabitWeek(s) added successfully (from {} to {})",
                    savedWeeks.size(),
                    savedWeeks.get(0).getStartWeek(),
                    savedWeeks.get(savedWeeks.size() - 1).getEndWeek());
        } else {
            log.error("Failed to add Habit Week data");
        }
    }



    private void addDaysToDB() {
        log.debug("addDaysToDB called ");
        Optional<HabitCalendar> calendar = habitCalendarService.getEntityLastCalendar();

        if (calendar.isEmpty()) {
            if (!habitCalendarService.saveEntityAll(dayAutoGenerator.dayAutoGenerator(LocalDate.now())).isEmpty())
                log.info("Habit Calendar initialized successfully");
            else
                log.error("Failed to initialize Habit Calendar");
        } else {
            LocalDate lastDate = calendar.get().getDate();

            if (lastDate.equals(LocalDate.now())) {
                log.info("All days already exist.");
            } else if (lastDate.isBefore(LocalDate.now())) {
                if (!habitCalendarService.saveEntityAll(dayAutoGenerator.dayAutoGenerator(lastDate.plusDays(1))).isEmpty())
                    log.info("Habit Calendar data added successfully");
                else
                    log.error("Failed to add data (Habit Calendar)");
            }
        }
    }

    private void addRolesToDB(){
        log.debug("addRolesToDB called ");
        List<Role> roleList = List.of(
                Role.builder().name("ROLE_VIEWER").description("Read-only access. Can view data but cannot modify anything.").build(),
                Role.builder().name("ROLE_USER").description("Regular user role. Can only log in, create, update, and track their own habits.").build(),
                Role.builder().name("ROLE_ADMIN").description("System administrator. Can manage all users and categories and view global analytics.").build());
        if (roleService.addOrUpdateRoleList(roleList))
            log.info("Roles data added successfully");
        else log.error("Failed to add data (Roles)");
    }

}
